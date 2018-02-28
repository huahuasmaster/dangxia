package dangxia.com.adapter.listener;

import dangxia.com.entity.TaskDto;

public interface TaskItemCallBackListener {
    void onIcon(int userId);

    void onMain(TaskDto taskDto);
}
