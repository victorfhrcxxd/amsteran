package phantom.task;

import java.util.List;

import phantom.FakePlayerTaskManager;

public class AITaskRunner implements Runnable
{	
	@Override
	public void run()
	{		
		FakePlayerTaskManager.INSTANCE.adjustTaskSize();
		List<AITask> aiTasks = FakePlayerTaskManager.INSTANCE.getAITasks();		
		aiTasks.forEach(aiTask -> ThreadPool.execute(aiTask));
	}	
}