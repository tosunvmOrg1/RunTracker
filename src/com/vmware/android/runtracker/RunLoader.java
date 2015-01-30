package com.vmware.android.runtracker;

import android.content.Context;

//Class for custom AsyncTaskLoader<Run> implementation via DataLoader<Run>
//=========================
public class RunLoader extends DataLoader<Run> {

    private long mRunId;
    
    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }
    
    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }

}
