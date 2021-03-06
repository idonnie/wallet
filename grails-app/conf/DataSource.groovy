

dataSource {

	pooled = true
	driverClassName = "org.h2.Driver"
	username = "sa"
	password = ""
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

// environment specific settings
environments {
	try {

		development {
			dataSource {
				dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
				url = "jdbc:h2:mem:devDb;MVCC=TRUE"
			}
		}
		test {
			dataSource {
				dbCreate = "update"
				url = "jdbc:h2:mem:testDb;MVCC=TRUE"
			}
		}

		production {
			dataSource {
				dbCreate = "update"
				def url = { -> "jdbc:h2:file:" + IO.getDbPath() + ";MVCC=TRUE" }
				// url = "jdbc:h2:prodDb;MVCC=TRUE"
				pooled = true
				properties {
					maxActive = -1
					minEvictableIdleTimeMillis=1800000
					timeBetweenEvictionRunsMillis=1800000
					numTestsPerEvictionRun=3
					testOnBorrow=true
					testWhileIdle=true
					testOnReturn=true
					validationQuery="SELECT 1"
				}
			}
		}

	} catch (Throwable t) {
		try {
			IO.writeString(new File(IO.getLogsPath() + "/data_source_environments_groovy.log"), NubLogger.toStringWithTimestamp(t))
		} finally {
			NubLogger.handle(t);
		}
	}


}
