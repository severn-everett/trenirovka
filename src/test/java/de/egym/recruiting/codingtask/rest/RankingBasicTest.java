package de.egym.recruiting.codingtask.rest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.google.inject.Inject;

import de.egym.recruiting.codingtask.AbstractIntegrationTest;
import de.egym.recruiting.codingtask.TestClientService;
import de.egym.recruiting.codingtask.exceptions.AlreadyExistsException;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Ignore;

public class RankingBasicTest extends AbstractIntegrationTest {

	@Inject
	private TestClientService testClientService;

	@Test
	public void testRangingList() throws AlreadyExistsException {
		final long userId1 = 20L;
		final long userId2 = 21L;

		final Exercise exercise1ToInsert = new Exercise();
		exercise1ToInsert.setDescription("Coding Task");
		exercise1ToInsert.setDuration(14400);
		exercise1ToInsert.setDistance(0);
		exercise1ToInsert.setCalories(500);
		exercise1ToInsert.setStartTime(Calendar.getInstance().getTime());
		exercise1ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise1ToInsert.setUserId(userId1);

		final Exercise persistedExercise1 = testClientService.createExercise(exercise1ToInsert);
		assertNotNull(persistedExercise1);
		assertNotNull(persistedExercise1.getId());

		final Exercise exercise2ToInsert = new Exercise();
		exercise2ToInsert.setDescription("Onsite Interview");
		exercise2ToInsert.setDuration(7200);
		exercise2ToInsert.setDistance(1500);
		exercise2ToInsert.setCalories(700);
		exercise2ToInsert.setStartTime(Calendar.getInstance().getTime());
		exercise2ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise2ToInsert.setUserId(userId2);

		final Exercise persistedExercise2 = testClientService.createExercise(exercise2ToInsert);
		assertNotNull(persistedExercise2);
		assertNotNull(persistedExercise2.getId());

		final List<Long> ranking = testClientService.getRanking(Arrays.asList(userId1, userId2));
		assertNotNull(ranking);
		assertThat(ranking.size(), is(2));
		assertThat(ranking.get(0), is(userId2));
		assertThat(ranking.get(1), is(userId1));
	}

        @Test
	public void testComplexRangeList() throws AlreadyExistsException {
		final long userId1 = 20L;
		final long userId2 = 21L;
                
		final Exercise exercise3ToInsert = new Exercise();
		exercise3ToInsert.setDescription("Code Example");
		exercise3ToInsert.setDuration(80);
		exercise3ToInsert.setDistance(15);
		exercise3ToInsert.setCalories(60);
		exercise3ToInsert.setStartTime(DateUtils.addDays(Calendar.getInstance().getTime(), -1));
		exercise3ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise3ToInsert.setUserId(userId1);
                
		final Exercise persistedExercise3 = testClientService.createExercise(exercise3ToInsert);
		assertNotNull(persistedExercise3);
		assertNotNull(persistedExercise3.getId());

		final List<Long> ranking = testClientService.getRanking(Arrays.asList(userId1, userId2));
		assertNotNull(ranking);
		assertThat(ranking.size(), is(2));
		assertThat(ranking.get(0), is(userId2));
		assertThat(ranking.get(1), is(userId1));
	}
}
