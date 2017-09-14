package de.egym.recruiting.codingtask.rest;

import de.egym.recruiting.codingtask.exceptions.AlreadyExistsException;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import io.swagger.annotations.Api;
import java.text.ParseException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

@Path("/api/v1/exercise")
@Api(value = "Exercise Service")
public interface ExerciseService {

	/**
	 * Get the exercise for a given exerciseId.
	 *
	 * @param exerciseId
	 *            id to search
	 * @return the exercise for the given exerciseId
	 */
	@GET
	@Path("/{exerciseId}")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	Exercise getExerciseById(@Nonnull @PathParam("exerciseId") Long exerciseId);
        
        /**
         * Update an exercise.
         * @param exercise
         *              the exercise to be updated
         * @return the updated exercise
         */
        @POST
        @Path("/{exerciseId}")
        @Nonnull
        @Produces(MediaType.APPLICATION_JSON)
        Exercise updateExercise(@Nonnull Exercise exercise);
        
        /**
         * Delete an exercise for a given exerciseId.
         * @param exerciseId
         *              id to search
         */
        @DELETE
        @Path("/{exerciseId}")
        @Produces(MediaType.APPLICATION_JSON)
        void deleteExercise(@Nonnull @PathParam("exerciseId") Long exerciseId);
        
	/**
	 * Get the exercises with the given description.
	 *
	 * @param description
	 *            description to search
	 * @return the exercises for the given description
	 */
	@GET
	@Path("/")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	List<Exercise> getExerciseByDescription(@Nullable @QueryParam("description") String description);
        
        /**
         * Create an exercise
         * @param exercise
         *              the exercise to be created
         * @return the created exercise
         * @throws AlreadyExistsException 
         */
        @PUT
        @Path("/")
        @Nonnull
        @Produces(MediaType.APPLICATION_JSON)
        Exercise createExercise(
            @Nonnull Exercise exercise 
            ) throws AlreadyExistsException;
        
        /**
         * Find all exercises for a user and optional search parameters.
         * @param userId
         *              the user's id
         * @param type
         *              (Optional) the type of exercises to list
         * @param date
         *              (Optional) the date in which to search
         * @return a list of the exercises
         * @throws ParseException 
         */
        @GET
        @Path("/users/viewexercises")
        @Produces(MediaType.APPLICATION_JSON)
        List<Exercise> getExercisesByUser (
                @Nonnull @QueryParam("userId") Long userId, 
                @Nullable @QueryParam("type") Enums.ExerciseType type,
                @Nullable @QueryParam("date") String date) throws ParseException;
        
        /**
         * Get a ranking of users by the points of their exercises completed in the past month.
         * @param userIds
         *              the list of users to rank
         * @return the list of user ids in descending order
         */
        @GET
        @Path("/users/getrankings")
        @Produces(MediaType.APPLICATION_JSON)
        List<Long> getUserRankings(@Nonnull @QueryParam("userIds") List<Long> userIds);
}
