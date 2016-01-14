package com.example.idiomsandphrases;



import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IdiomsListActivity extends Activity implements
		AdapterView.OnItemClickListener, TextWatcher {
	public static final String KEY_CATEGORY_NAME = "categoryname";
	private ArrayAdapter<IdiomPreview> adaptor;
	private String category = null;
	private String language = null;
	private Intent pendingIntent;
	private RelativeLayout mainLayout;
	private RelativeLayout indicatorLayout;
	
	private long languageId = 0;
	private long categoryId = 0;
	


	private void launchPendingIntent() {
		Intent localIntent = this.pendingIntent;
		this.pendingIntent = null;
		if (localIntent != null) {
			startActivity(localIntent);
		}
	}

	public void afterTextChanged(Editable paramEditable) {
		this.adaptor.getFilter().filter(paramEditable.toString());
	}

	public void beforeTextChanged(CharSequence paramCharSequence,
			int paramInt1, int paramInt2, int paramInt3) {
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.idiomlist);
		
		mainLayout = (RelativeLayout)findViewById(R.id.smslistparent);
		indicatorLayout = ActivityIndicator.getActivityIndicatorLayout(this);
		
		this.language = getIntent().getExtras().getString("langname");
		this.category = getIntent().getExtras().getString("categoryname");
		
		ActionBar actionBar = getActionBar();
		ActionBarProperties.setupActionBar(this, actionBar, category, language, true);
		
		ListView localListView = (ListView) findViewById(R.id.idiomlist);
		
		this.adaptor = new IdiomsListAdaptor(this, R.layout.categoryitem, IdiomsDataSource
				.getInstance(this).getIdiomsForCategory(language, category));
		localListView.setAdapter(this.adaptor);
		int[] arrayOfInt = { 16711680, 16711680, 16711680 };
		localListView.setDivider(new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, arrayOfInt));
		localListView.setDividerHeight(2);
		localListView.setOnItemClickListener(this);
		((EditText) findViewById(R.id.search)).addTextChangedListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem item1 = menu.add("Add New Idiom");
		item1.setIcon(R.drawable.add_icon);
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent localIntent = new Intent(IdiomsListActivity.this, AddIdiomActivity.class);
				localIntent.putExtra("langname", language);
				localIntent.putExtra("categoryname", category);
				IdiomsListActivity.this.pendingIntent = localIntent;
				IdiomsListActivity.this.launchPendingIntent();
				IdiomsListActivity.this.finish();
				return true;
			}
		});
		MenuItem item2 = menu.add("Delete "+category);
		item2.setIcon(R.drawable.delete_icon);
		item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						IdiomsListActivity.this);
				alertDialogBuilder.setTitle("Delete Category");

				// set dialog message
				alertDialogBuilder
						.setMessage("Are you sure to delete " + category)
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										DataTask task = new DataTask();
										task.execute();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();

				alertDialog.show();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	    	goBack();
	        break;

	    }
	    return true;
	}
	
	private class DataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			IdiomsDataSource.getInstance(IdiomsListActivity.this).deleteCategory(language, category);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mainLayout.removeView(indicatorLayout);
			IdiomsListActivity.this.goBack();
		}
		
	}

	private void goBack() {
		Intent localIntent = new Intent(IdiomsListActivity.this, IdiomsCategoryListActivity.class);
		localIntent.putExtra("langid", languageId);
		localIntent.putExtra("langname", language);
		pendingIntent = localIntent;
		launchPendingIntent();
		finish();
	}


	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		Intent localIntent = new Intent(this, IdiomsDisplayerActivity.class);
		localIntent.putExtra("langname", language);
		localIntent.putExtra("categoryname", category);
		localIntent.putExtra("idiomid",
				((IdiomPreview) this.adaptor.getItem(paramInt)).getIdiomName());
		localIntent.putExtra("index",
				((IdiomPreview) this.adaptor.getItem(paramInt)).getIndex());
		this.pendingIntent = localIntent;
		launchPendingIntent();
		finish();
	}


	public void onTextChanged(CharSequence paramCharSequence, int paramInt1,
			int paramInt2, int paramInt3) {
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
