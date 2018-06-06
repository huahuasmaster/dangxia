package dangxia.com.adapter.listener;

import dangxia.com.dto.TaskDto;

public interface TaskItemCallBackListener {
    void onIcon(int userId);

    void onMain(TaskDto taskDto);
}
