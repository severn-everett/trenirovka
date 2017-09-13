package de.egym.recruiting.codingtask.jpa.domain;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang3.time.DateUtils;

@Entity
public class Exercise extends AbstractEntity {
    
        private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	private static final long serialVersionUID = 1L;
        
        @NotNull
        @Min(value = 0)
	private Long userId;

        @NotNull
        @Pattern(regexp = "^[A-Za-z0-9\\s]*$")
	private String description;
        
	@Enumerated(EnumType.STRING)
	private Enums.ExerciseType type;

	/**
	 * format: yyyy-MM-dd'T'HH:mm:ss
	 */
        @NotNull
	private Date startTime;

	/**
	 * in seconds
	 */
        @NotNull
        @Min(value = 0)
	private Integer duration;
        
        /**
         * Aggregated from startTime + duration
         */
        private Date endTime;

	/**
	 * in meters
	 */
        @NotNull
        @Min(value = 0)
	private Integer distance;

	/**
	 * in kcal
	 */
        @NotNull
        @Min(value = 0)
	private Integer calories;

	public Integer getCalories() {
		return calories;
	}

	public void setCalories(Integer calories) {
		this.calories = calories;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Enums.ExerciseType getType() {
		return type;
	}

	public void setType(Enums.ExerciseType type) {
		this.type = type;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
        
        public void setEndTime(Date endTime) {
                this.endTime = endTime;
        }
        
        public Date getEndTime() {
                return endTime;
        }

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
        
        public static Date parseDate(String dateTimeStr) throws ParseException {
            return DateUtils.parseDate(dateTimeStr, TIME_PATTERN);
        }
        
        public static Date calculateEndTime(Date startTime, Integer duration) {            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            calendar.add(Calendar.SECOND, duration);
            return calendar.getTime();
        }
}
