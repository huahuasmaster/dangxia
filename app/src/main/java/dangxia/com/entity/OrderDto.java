package dangxia.com.entity;

public class OrderDto {
    private int id;

    private int executorId;

    private String orderDate;

    private int status;

    private TaskDto taskDto;

    private String executorName;

    private String finishDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExecutorId() {
        return executorId;
    }

    public void setExecutorId(int executorId) {
        this.executorId = executorId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public TaskDto getTaskDto() {
        return taskDto;
    }

    public void setTaskDto(TaskDto taskDto) {
        this.taskDto = taskDto;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", executorId=" + executorId +
                ", orderDate='" + orderDate + '\'' +
                ", status=" + status +
                ", taskDto=" + taskDto +
                ", executorName='" + executorName + '\'' +
                ", finishDate='" + finishDate + '\'' +
                '}';
    }
}
