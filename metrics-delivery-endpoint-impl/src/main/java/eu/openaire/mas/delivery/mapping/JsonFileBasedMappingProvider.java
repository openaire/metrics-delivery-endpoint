package eu.openaire.mas.delivery.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Reads mappings from a set of files stored in a given directory.
 * Each filename identifies single group and encapsulates multiple metrics mappings.s
 * @author mhorst
 *
 */
@Service
public class JsonFileBasedMappingProvider implements MappingProvider, FileChangeListener {

    @Value("${metrics.mapping.location}")
    private Resource fileLocation;
    
    private static final Logger log = LoggerFactory.getLogger(JsonFileBasedMappingProvider.class);
    
    /**
     * Internal mapping identified by groupId and metricId on a 2nd map level.
     */
    private Map<String, Map<String,PrometheusMetricMeta>> metricMappings = new ConcurrentHashMap<String, Map<String,PrometheusMetricMeta>>();
    
    @PostConstruct
    public void initialize() {
        initializeMappings();
    }

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
    
    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        for(ChangedFiles cFiles : changeSet) {
            for(ChangedFile cFile: cFiles.getFiles()) {
                if(isMappingFile(cFile)) {
                    if (cFile.getType().equals(Type.DELETE)) {
                        metricMappings.clear();
                    } else if (cFile.getType().equals(Type.ADD) || cFile.getType().equals(Type.MODIFY)) {
                        initializeMappings();
                    }
                    log.info("Operation: " + cFile.getType() 
                      + " On file: "+ cFile.getFile().getAbsolutePath() + " is done");
                    return;
                }
            }
        }
    }
    
    /**
     * Checks whether notification is related to a mapping file.
     */
    private boolean isMappingFile(ChangedFile cFile) {
        return fileLocation.getFilename().contentEquals(cFile.getRelativeName());
    }
    
    @Override
    public PrometheusMetricMeta get(String groupId, String metricId) {
        Map<String, PrometheusMetricMeta> groupMap = metricMappings.get(groupId);
        if (groupMap != null) {
            PrometheusMetricMeta result = groupMap.get(metricId);
            if (result != null) {
                return result;
            } else {
                throw new NoSuchElementException("unidentified metric: " + metricId + 
                        " withing the group: " + groupId);
            }
        } else {
            throw new NoSuchElementException("unidentified group: " + groupId);    
        }
    }
    
    @Override
    public Set<String> listMetrics(String groupId) {
        Map<String, PrometheusMetricMeta> groupMap = metricMappings.get(groupId);
        if (groupMap != null) {
            return groupMap.keySet();
        } else {
            throw new NoSuchElementException("unidentified group: " + groupId);    
        }
    }
    
    static Map<String, Map<String, PrometheusMetricMeta>> getMap(String jsonContent) {
        Gson gson = new Gson();
        java.lang.reflect.Type empMapType = new TypeToken<Map<String, Map<String, PrometheusMetricMeta>>>() {}.getType();
        return gson.fromJson(jsonContent, empMapType);
    }

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
}
