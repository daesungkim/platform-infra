// File: environment.groovy
environments {

    dev {
        // serverName = 'http://localhost:8080'
        nexus {
            publicRepository = 'http://dev.nexus.com/nexus/content/groups/public'
            snapshotRepository = 'http://dev.nexus.com/nexus/content/repositories/ncsoft-snapshots'
            repository = 'http://dev.nexus.com/nexus/content/repositories/ncsoft-releases'
            userName = 'admin'
            password = 'admin123'
        }        
    }
    
    live {
        nexus {
            publicRepository = 'http://live.nexus.com/nexus/content/groups/public'
            snapshotRepository = 'http://live.nexus.com/nexus/content/repositories/linkedhole-snapshots'
            repository = 'http://live.nexus.com/nexus/content/repositories/linkedhole-releases'
            userName = 'admin'
            password = 'admin123'
        }   
    }
}    