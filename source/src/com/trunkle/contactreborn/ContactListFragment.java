package com.trunkle.contactreborn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trunkle.contactreborn.ContactListView.ContactListThread;

public class ContactListFragment extends Fragment {

	private ContactListView mContactView;
	
	private ContactListThread mContactThread;
	
	private BasicGLSurfaceView mView;
	
	public ContactListFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a new TextView and set its text to the fragment's section
		// number argument value.
		View view = inflater.inflate(R.layout.contact_list,
		        container, false);
		
		//mView = new BasicGLSurfaceView(getActivity().getApplication());
		    return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		// get handles to the LunarView from XML, and its LunarThread
		//mContactView = (ContactListView) getView().findViewById(R.id.contactListView);
		//mContactThread = mContactView.getThread();

        // give the LunarView a handle to the TextView used for messages
		//mContactView.setTextView((TextView) getView().findViewById(R.id.text));

//        if (savedInstanceState == null) {
//            // we were just launched: set up a new game
//        	mContactThread.setState(ContactListThread.STATE_RUNNING);
//            Log.w(this.getClass().getName(), "SIS is null");
//        } else {
//            // we are being restored: resume a previous game
//        	mContactThread.restoreState(savedInstanceState);
//            Log.w(this.getClass().getName(), "SIS is nonnull");
//        }
	}
}
