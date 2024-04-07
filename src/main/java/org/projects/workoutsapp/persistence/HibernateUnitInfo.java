package org.projects.workoutsapp.persistence;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

import javax.sql.DataSource;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class HibernateUnitInfo implements PersistenceUnitInfo {
	@Override
	public String getPersistenceUnitName() {
		return "dbConnection";
	}
	
	@Override
	public String getPersistenceProviderClassName() {
		return "org.hibernate.jpa.HibernatePersistenceProvider";
	}
	
	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return PersistenceUnitTransactionType.RESOURCE_LOCAL;
	}
	
	@Override
	public DataSource getJtaDataSource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:sqlite:src/main/resources/sql/data.sqlite");
		ds.setUsername("");
		ds.setPassword("");
		return ds;
	}
	
	@Override
	public DataSource getNonJtaDataSource() {
		return null;
	}
	
	@Override
	public List<String> getMappingFileNames() {
		return null;
	}
	
	@Override
	public List<URL> getJarFileUrls() {
		return null;
	}
	
	@Override
	public URL getPersistenceUnitRootUrl() {
		return null;
	}
	
	@Override
	public List<String> getManagedClassNames() {
		return List.of("org.projects.workoutsapp.entities.SetRecord",
				"org.projects.workoutsapp.entities.ExerciseRecord",
				"org.projects.workoutsapp.entities.WorkoutRecord",
				"org.projects.workoutsapp.entities.Exercise");
	}
	
	@Override
	public boolean excludeUnlistedClasses() {
		return false;
	}
	
	@Override
	public SharedCacheMode getSharedCacheMode() {
		return null;
	}
	
	@Override
	public ValidationMode getValidationMode() {
		return null;
	}
	
	@Override
	public Properties getProperties() {
		return null;
	}
	
	@Override
	public String getPersistenceXMLSchemaVersion() {
		return null;
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return null;
	}
	
	@Override
	public void addTransformer(ClassTransformer classTransformer) {
	
	}
	
	@Override
	public ClassLoader getNewTempClassLoader() {
		return null;
	}
}
