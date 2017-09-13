package de.egym.recruiting.codingtask.sort;

import de.egym.recruiting.codingtask.jpa.domain.Exercise;
import java.util.List;

public interface SortingService {
    
    List<Long> sortUsersByActivity(List<Exercise> exercises);
    
}
