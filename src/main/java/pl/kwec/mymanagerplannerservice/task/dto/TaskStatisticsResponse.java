package pl.kwec.mymanagerplannerservice.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatisticsResponse {

    private long completedCount;
    private long pendingCount;
    private long totalCount;
    private double completionPercentage;
}
