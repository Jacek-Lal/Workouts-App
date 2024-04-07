package org.projects.workoutsapp.persistence;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.projects.workoutsapp.controllers.components.SetController;
import org.projects.workoutsapp.controllers.components.SingleExerciseController;
import org.projects.workoutsapp.dto.WorkoutWrapUpInfo;
import org.projects.workoutsapp.entities.ExerciseRecord;
import org.projects.workoutsapp.entities.SetRecord;
import org.projects.workoutsapp.entities.WorkoutRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseClient {
	private static <T> List<T> getData(String jpql, Class<T> className){
		Map<String, String> props = new HashMap<>();
		props.put("hibernate.show_sql", "true");
		
		try (EntityManagerFactory emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new HibernateUnitInfo(), props)) {
			try (EntityManager em = emf.createEntityManager()) {
				em.getTransaction().begin();
				
				TypedQuery<T> query = em.createQuery(jpql, className);
				
				em.getTransaction().commit();
				
				return query.getResultList();
			}
		}
	}
	public static List<WorkoutRecord> getWorkouts(){
		String jpql = "SELECT w FROM WorkoutRecord w";
		return getData(jpql, WorkoutRecord.class);
	}
	public static List<WorkoutWrapUpInfo> getWrapUpData(){
		String jpql = """
					SELECT NEW org.projects.workoutsapp.dto.WorkoutWrapUpInfo(w, SUM(s.weight), COUNT(s.id))
					FROM WorkoutRecord w
					JOIN w.exercises e
					JOIN e.sets s
					GROUP BY w
					""";
		
		return getData(jpql, WorkoutWrapUpInfo.class);
	}
	public static List<ExerciseRecord> getWorkoutDataById(Integer id){
		Map<String, String> props = new HashMap<>();
		props.put("hibernate.show_sql", "true");
		
		try (EntityManagerFactory emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new HibernateUnitInfo(), props)) {
			try (EntityManager em = emf.createEntityManager()) {
				em.getTransaction().begin();
				
				EntityGraph<?> graph = em.createEntityGraph(ExerciseRecord.class);
				graph.addAttributeNodes("sets");
				
				String jpql = """
  					SELECT e
  					FROM ExerciseRecord e
  					WHERE e.workout.id = :id
  					""";
				TypedQuery<ExerciseRecord> query = em.createQuery(jpql, ExerciseRecord.class)
														   .setHint("jakarta.persistence.loadgraph", graph);
				query.setParameter("id", id);
				em.getTransaction().commit();
				
				return query.getResultList();
			}
		}
		
	}
	public static void saveWorkout(WorkoutRecord workoutRecord, List<SingleExerciseController> exerciseControllers){
		if(exerciseControllers.isEmpty()) return;
		
		Map<String, String> props = new HashMap<>();
		props.put("hibernate.show_sql", "true");
		
		try (EntityManagerFactory emf = new HibernatePersistenceProvider().createContainerEntityManagerFactory(new HibernateUnitInfo(), props)) {
			try (EntityManager em = emf.createEntityManager()) {
				em.getTransaction().begin();
				
				for(SingleExerciseController exerciseController : exerciseControllers){
					if(!exerciseController.isValid()) continue;
					
					String exerciseName = exerciseController.exerciseName.getText();
					String exerciseDesc = exerciseController.descriptionField.getText();
					ExerciseRecord exerciseRecord = new ExerciseRecord(exerciseName, exerciseDesc);
					
					for(SetController setController : exerciseController.allSets){
						if(!setController.isValid()) continue;
						
						SetRecord setRecord = new SetRecord(setController.getId(), setController.getWeight(), setController.getReps());
						
						setRecord.setExercise(exerciseRecord);
						exerciseRecord.addSet(setRecord);
					}
					
					if(!exerciseRecord.getSets().isEmpty()){
						exerciseRecord.setWorkout(workoutRecord);
						workoutRecord.addExercise(exerciseRecord);
					}
					
				}
				
				if(!workoutRecord.getExercises().isEmpty())
					em.persist(workoutRecord);
				
				em.getTransaction().commit();
				
			}
		}
	}
}
