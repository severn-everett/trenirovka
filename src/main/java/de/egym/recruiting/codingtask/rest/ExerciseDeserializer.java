package de.egym.recruiting.codingtask.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public class ExerciseDeserializer extends StdDeserializer<Exercise> {
    
    public ExerciseDeserializer(Class<Exercise> e) {
        super(e);
    }

    @Override
    public Exercise deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        Exercise exercise = new Exercise();
        Optional.ofNullable(node.get("userId")).ifPresent(userIdNode -> exercise.setUserId(userIdNode.longValue()));
        Optional.ofNullable(node.get("description")).ifPresent(descriptionNode -> exercise.setDescription(descriptionNode.asText()));
        Optional.ofNullable(node.get("type")).ifPresent(typeNode -> {
            try {
                exercise.setType(Enums.ExerciseType.valueOf(typeNode.asText()));
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("type '" + typeNode.asText() + "' is invalid - must be one of the following:"
                        + " [RUNNING, CYCLING, SWIMMING, ROWING, WALKING, CIRCUIT_TRAINING, STRENGTH_TRAINING, FITNESS_COURSE, SPORTS, OTHER]");
            }
        });
        Optional.ofNullable(node.get("startTime")).ifPresent(startTimeNode -> {
            try {
                exercise.setStartTime(Exercise.parseDate(startTimeNode.asText()));
            } catch (ParseException pe) {
                throw new IllegalArgumentException("startTime '" + startTimeNode.asText() + "' is invalid - must be the following format: " + Exercise.TIME_PATTERN);
            }
        });
        Optional.ofNullable(node.get("duration")).ifPresent(durationNode -> exercise.setDuration(durationNode.asInt()));
        Optional.ofNullable(node.get("distance")).ifPresent(distanceNode -> exercise.setDistance(distanceNode.asInt()));
        Optional.ofNullable(node.get("calories")).ifPresent(caloriesNode -> exercise.setCalories(caloriesNode.asInt()));
        
        return exercise;
    }
    
}
