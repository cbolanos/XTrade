package com.xtrade.android;

import org.apache.commons.lang.StringUtils;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.xtrade.android.provider.DatabaseContract;
import com.xtrade.android.provider.DatabaseContract.Contact;
import com.xtrade.android.provider.DatabaseContract.ContactColumns;
import com.xtrade.android.provider.DatabaseContract.ContactTypeColumns;
import com.xtrade.android.util.EventConstant;
import com.xtrade.android.util.Settings;

public class ContactCreateOrUpdateActivity extends BaseActivity implements EventConstant {

	private Intent intent;
	
	private SimpleCursorAdapter adapter;
	
	private EditText etxContactName;
	private Spinner spnContactType;
	private EditText etxContactEmail;
	private EditText etxContactPhone;
	
	public void onCreate(Bundle savedIntanceState) {
		super.onCreate(savedIntanceState);
		setContentView(R.layout.contact);
		
		Cursor cursor = getContentResolver().query(DatabaseContract.ContactType.CONTENT_URI, 
				new String[] {BaseColumns._ID, ContactTypeColumns.CONTACT_TYPE_ID, ContactTypeColumns.NAME},
				null, 
				null,
				null);
		
		adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[] {ContactTypeColumns.NAME}, new int[] {android.R.id.text1}, 0);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnContactType = (Spinner) findViewById(R.id.spnContactType);
		spnContactType.setAdapter(adapter);
		
		etxContactName = (EditText) findViewById(R.id.etxContactName);
		etxContactEmail = (EditText) findViewById(R.id.etxContactEmail);
		etxContactPhone = (EditText) findViewById(R.id.etxContactPhone);
		
		// We get the intent which calls the activity
		intent = getIntent();
		
		// We get the ACTION_TYPE extra which tells us what operation we should perform (Save or Update)
		int extra = intent.getIntExtra("ACTION_TYPE", -1);
		
		// Setting default values while we're on developer mode
		if (Settings.DEBUG && extra == CONTACT_CREATE_REQUEST_CODE) {
			etxContactName.setText("Jos� Luis Ayerdis Espinoza");
			etxContactEmail.setText("joserayerdis@gmail.com");
			etxContactPhone.setText("86727076");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.contact_menu, _menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem _menuItem) {
		switch (_menuItem.getItemId()) {
		case R.id.mniSaveContact:
			// We get the ACTION_TYPE extra which tells us what operation we should perform (Save or Update)
			int extra = intent.getIntExtra("ACTION_TYPE", -1);

			CursorWrapper cursorWrapper = (CursorWrapper) spnContactType.getSelectedItem();
			
			String contactName = etxContactName.getText().toString();
			String contactType = cursorWrapper.getString(cursorWrapper.getColumnIndexOrThrow(ContactTypeColumns.NAME));
			String contactEmail = etxContactEmail.getText().toString();
			String contactPhone = etxContactPhone.getText().toString();
			
			// Evaluating if the EditTexts' content is empty or not
			if (!StringUtils.isEmpty(contactName) && !StringUtils.isEmpty(contactType) && !StringUtils.isEmpty(contactEmail) && !StringUtils.isEmpty(contactPhone)) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(ContactColumns.NAME, contactName);
				contentValues.put(ContactColumns.TYPE, contactType);
				contentValues.put(ContactColumns.EMAIL, contactEmail);
				contentValues.put(ContactColumns.PHONE, contactPhone);
				
				Uri contactUri = null;
				
				// We build a result variable which will be set on default value for canceled
				int result = RESULT_CANCELED;
				
				if (extra == CONTACT_CREATE_REQUEST_CODE) {
					contactUri = getContentResolver().insert(Contact.CONTENT_URI, contentValues);
					result = contactUri == null ? RESULT_CANCELED : RESULT_OK;
				}
				
				setResult(result);
				finish();
			}
			
			return true;
		default:
			return super.onOptionsItemSelected(_menuItem);
		}
	}
	
}