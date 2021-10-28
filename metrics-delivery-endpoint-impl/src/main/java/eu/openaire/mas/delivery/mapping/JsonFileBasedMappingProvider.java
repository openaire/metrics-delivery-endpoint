package eu.openaire.mas.delivery.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Reads mappings from a set of files stored in a given directory.
 * Each filename identifies single resource and encapsulates multiple metrics mappings.s
 * @author mhorst
 *
 */
@Service
public class JsonFileBasedMappingProvider implements MappingProvider {

    @Value("${metrics.mapping.location}")
    private Resource fileLocation;
    
    private static final Logger log = LoggerFactory.getLogger(JsonFileBasedMappingProvider.class);
    
    /**
     * Internal mapping identified by resourceId and metricId on a 2nd map level.
     */
    private Map<String, Map<String,PrometheusMetricMeta>> metricMappings = new ConcurrentHashMap<String, Map<String,PrometheusMetricMeta>>();
    
    private ExecutorService fileWatcherExecutorService;
    
    @PostConstruct
    public void initialize() {
        initializeMappings();
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            
            Path path = fileLocation.getFile().getParentFile().toPath();

            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            
            fileWatcherExecutorService = Executors.newSingleThreadExecutor();
            fileWatcherExecutorService.execute(new MappingFileWatcher(watchService));    
        } catch (IOException e) {
            throw new RuntimeException("unable to initialize mapping watcher service", e);
        }
        
    }
    
    @PreDestroy
    public void destroy() {
        log.debug("shutting down mapping file watcher");
        fileWatcherExecutorService.shutdownNow();
    }
    
    @Override
    public PrometheusMetricMeta get(String resourceId, String metricId) throws MappingNotFoundException {
        Map<String, PrometheusMetricMeta> resourceMap = metricMappings.get(resourceId);
        if (resourceMap != null) {
            PrometheusMetricMeta result = resourceMap.get(metricId);
            if (result != null) {
                return result;
            } else {
                throw new MappingNotFoundException(String.format("unidentified metric: '%s'" + 
                        " for the resource: '%s'", metricId, resourceId));
            }
        } else {
            throw new MappingNotFoundException(String.format("unidentified resource: '%s'", resourceId));
        }
    }
    
    @Override
    public Set<String> listMetrics(String resourceId) throws MappingNotFoundException {
        Map<String, PrometheusMetricMeta> resourceMap = metricMappings.get(resourceId);
        if (resourceMap != null) {
            return resourceMap.keySet();
        } else {
            throw new MappingNotFoundException("unidentified resource: " + resourceId);
        }
    }

    @Override
    public Set<String> listResources() {
	return new HashSet<>(metricMappings.keySet());
    }
    
    /**
     * Initializes in-memory mappings with the corresponding mapping file contents.
     */
    private void initializeMappings() {
        try {
            log.info("loading mappings from file: " + fileLocation.getURI().toString());
            Map<String, Map<String, PrometheusMetricMeta>> readMap = getMap(getResourceFileAsString(fileLocation));
            metricMappings.clear();
            for (Entry<String, Map<String,PrometheusMetricMeta>> entry : readMap.entrySet()) {
                metricMappings.put(entry.getKey(), new ConcurrentHashMap<String,PrometheusMetricMeta>(entry.getValue()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Checks whether notification is related to a mapping file.
     */
    private boolean isMappingFile(Path mappingFile) {
        return fileLocation.getFilename().contentEquals(mappingFile.toString());
    }
    
    /**
     * Converts mappings defined as JSON into the object representation.
     */
    static Map<String, Map<String, PrometheusMetricMeta>> getMap(String jsonContent) {
        Gson gson = new Gson();
        java.lang.reflect.Type empMapType = new TypeToken<Map<String, Map<String, PrometheusMetricMeta>>>() {}.getType();
        return gson.fromJson(jsonContent, empMapType);
    }

    /**
     * Loads contents of a given resource into String.
     */
    static String getResourceFileAsString(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            if (is == null) {
                throw new IOException("unable to get stream for a resource: " + resource.getURI());
            }
            return getInputStreamContents(is);
        }
    }
    
    static String getResourceFileAsString(String fileName) throws IOException {
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IOException("unable to get stream for a file: " + fileName);
            }
            return getInputStreamContents(is);
        }
    }
    
    private static String getInputStreamContents(InputStream is) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(is, "utf8"); BufferedReader reader = new BufferedReader(isr)) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
    
    /**
     * Watcher class responsible for handling mapping file modifications by syncing
     * in memory mapping state with the file contents.
     *
     */
    class MappingFileWatcher implements Runnable {
        
        private WatchService watchService;

        public MappingFileWatcher(WatchService watchService) {
            this.watchService = watchService;
        }
        
        @Override
        public void run() {
            try {
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {

                        if (isMappingFile((Path) event.context())) {
                            if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                log.info("handling file related event by removing all the mappings");
                                metricMappings.clear();
                            } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)
                                    || event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                log.info("handling file related event by reinitializing the mappings");
                                initializeMappings();
                            } else {
                                log.warn("unsupported event kind: " + event.kind());
                            }
                            log.info("hanlding operation: " + event.kind() + " on file: " + event.context() + " is done");
                        } else {
                            log.debug("got notification unrelated to the mapping file: " + event.context());
                        }

                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                log.info("got interrupted, finishing mapping file watcher job");
            }

            log.debug("mapping file watcher job is done");
        }
        
    }
    
}
