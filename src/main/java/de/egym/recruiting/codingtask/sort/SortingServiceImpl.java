package de.egym.recruiting.codingtask.sort;

import com.google.common.collect.ImmutableMap;
import de.egym.recruiting.codingtask.jpa.domain.Enums;
import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SortingServiceImpl implements SortingService {
    
    private static final Logger log = LoggerFactory.getLogger(SortingServiceImpl.class);
    
    private static final Map<Enums.ExerciseType, Double> TYPE_MULTIPLIERS = ImmutableMap.<Enums.ExerciseType, Double>builder()
                                                                                .put(Enums.ExerciseType.RUNNING, 2.0)
                                                                                .put(Enums.ExerciseType.CYCLING, 2.0)
                                                                                .put(Enums.ExerciseType.SWIMMING, 3.0)
                                                                                .put(Enums.ExerciseType.ROWING, 2.0)
                                                                                .put(Enums.ExerciseType.WALKING, 1.0)
                                                                                .put(Enums.ExerciseType.CIRCUIT_TRAINING, 4.0)
                                                                                .put(Enums.ExerciseType.STRENGTH_TRAINING, 3.0)
                                                                                .put(Enums.ExerciseType.FITNESS_COURSE, 2.0)
                                                                                .put(Enums.ExerciseType.SPORTS, 3.0)
                                                                                .put(Enums.ExerciseType.OTHER, 1.0)
                                                                                .build();
    
    @Override
    public List<Long> sortUsersByActivity(List<Exercise> exercises) {
        Map<Long, Double> userPointsMap = new HashMap<>();
        Map<Long, Map<Enums.ExerciseType, Integer>> typesCompletedMap = new HashMap<>();
        exercises.forEach(exercise -> {
            Long userId = exercise.getUserId();
            double exerciseScore = 0.0;
            exerciseScore += exercise.getCalories().doubleValue();
            exerciseScore += exercise.getDuration().doubleValue();
            
            Enums.ExerciseType type = exercise.getType();
            exerciseScore *= TYPE_MULTIPLIERS.get(type);
            
            typesCompletedMap.putIfAbsent(userId, new HashMap<>());
            Map<Enums.ExerciseType, Integer> typeCountMap = typesCompletedMap.get(userId);
            int typeCount = typeCountMap.getOrDefault(type, 0);
            
            // We want to be precise and subtract exactly 10%
            // each time from the multiplier
            BigDecimal repeatMultiplier = new BigDecimal(1);
            for (int i = 0; i < typeCount && i < 10; i++) {
                repeatMultiplier.add(new BigDecimal(-0.1));
            }
            exerciseScore = repeatMultiplier.multiply(BigDecimal.valueOf(exerciseScore)).doubleValue();
            typeCountMap.put(type, typeCount + 1);
            
            // Add the current score to the cumulative score
            double cumulativeScore = userPointsMap.getOrDefault(userId, 0.0);
            log.debug(String.format("Adding %s to %s for user %s", exerciseScore, cumulativeScore, userId));
            userPointsMap.put(userId, cumulativeScore + exerciseScore);
        });
        return userPointsMap.entrySet()
                .stream()
                .sorted((user1, user2) -> user2.getValue().compareTo(user1.getValue()))
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }
    
}
