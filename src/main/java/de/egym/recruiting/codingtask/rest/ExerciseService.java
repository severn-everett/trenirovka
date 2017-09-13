package de.egym.recruiting.codingtask.rest;

import de.egym.recruiting.codingtask.exceptions.AlreadyExistsException;
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
        
        @POST
        @Path("/{exerciseId}")
        @Nonnull
        @Produces(MediaType.APPLICATION_JSON)
        Exercise updateExercise(
            @Nonnull @PathParam("exerciseId") Long exerciseId,
            @Nonnull @QueryParam("userId") Long userId,
            @Nonnull @QueryParam("description") String description,
            @Nonnull @QueryParam("type") String type,
            @Nonnull @QueryParam("startTime") String startTime,
            @Nonnull @QueryParam("duration") Integer duration,
            @Nonnull @QueryParam("distance") Integer distance,
            @Nonnull @QueryParam("calories") Integer calories
        );
        
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
        
        @GET
        @Path("/users/viewuser/{userId}")
        @Produces(MediaType.APPLICATION_JSON)
        List<Exercise> getExercisesByUser(
                @Nonnull @PathParam("userId") Long userId, 
                @Nullable @QueryParam("type") String type,
                @Nullable @QueryParam("startTime") String startTime,
                @Nullable @QueryParam("endTime") String endTime);
        
        @GET
        @Path("/users/getrankings")
        @Produces(MediaType.APPLICATION_JSON)
        List<Long> getUserRankings();
        
        @PUT
        @Path("/create")
        @Nonnull
        @Produces(MediaType.APPLICATION_JSON)
        Exercise createExercise(
            @Nonnull @QueryParam("userId") Long userId,
            @Nonnull @QueryParam("description") String description,
            @Nonnull @QueryParam("type") String type,
            @Nonnull @QueryParam("startTime") String startTime,
            @Nonnull @QueryParam("duration") Integer duration,
            @Nonnull @QueryParam("distance") Integer distance,
            @Nonnull @QueryParam("calories") Integer calories
        ) throws AlreadyExistsException;
}
