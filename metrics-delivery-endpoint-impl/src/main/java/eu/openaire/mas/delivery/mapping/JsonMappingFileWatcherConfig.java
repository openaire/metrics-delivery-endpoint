package eu.openaire.mas.delivery.mapping;

import java.io.IOException;
import java.time.Duration;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Registers JSON file mapping watcher to react on mapping changes. 
 * @author mhorst
 *
 */
@Configuration
public class JsonMappingFileWatcherConfig {
    
    @Value("${metrics.mapping.location}")
    private Resource fileLocation;
    
    @Value("${metrics.mapping.watcher.poll.interval:10000}")
    private Long pollInterval;
    
    @Value("${metrics.mapping.watcher.quiet.period:5000}")
    private Long quietPeriod;
    
    private static final Logger log = LoggerFactory.getLogger(JsonMappingFileWatcherConfig.class);
    
    @Autowired
    FileChangeListener fileChangeListener;
    
    FileSystemWatcher fileSystemWatcher;
    
    @Bean
    public FileSystemWatcher fileSystemWatcher() {
        fileSystemWatcher = new FileSystemWatcher(true, 
                Duration.ofMillis(pollInterval),
                Duration.ofMillis(quietPeriod));
        try {
            fileSystemWatcher.addSourceDirectory(fileLocation.getFile().getParentFile());
            fileSystemWatcher.addListener(fileChangeListener);
            fileSystemWatcher.start();
            log.info("FileSystemWatcher started");
            return fileSystemWatcher;
        } catch (IOException e) {
            log.error("unable to access mapping file", e);
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        fileSystemWatcher.stop();
        log.info("FileSystemWatcher stopped");
    }
}
