package com.xtrade.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.xtrade.android.R;
import com.xtrade.android.provider.DatabaseContract.TraderEntity;
import com.xtrade.android.provider.DatabaseContract.TraderColumns;
import com.xtrade.android.util.ActionConstant;
import com.xtrade.android.util.EventConstant;

public class TraderListFragment extends SherlockFragment implements
		EventConstant, LoaderManager.LoaderCallbacks<Cursor> {

	private CursorAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View fragmentView = inflater.inflate(R.layout.trader_tab_list, container, false);

		adapter = new TraderAdapter(getActivity());

		ListView listView = (ListView) fragmentView.findViewById(R.id.lvwTrader);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent(ActionConstant.TRADER_DETAIL);
				intent.putExtra(TraderColumns.TRADER_ID, id);
				startActivity(intent);
			}
		});

		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Force start background query to load sessions
		getLoaderManager().restartLoader(0, null, this);
	}

	// LoaderCallbacks interface
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		Loader<Cursor> loader = new CursorLoader(getActivity(), TraderEntity.CONTENT_URI, null, null, null, TraderEntity.DEFAULT_SORT);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null)
			return;

		adapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	public class TraderAdapter extends CursorAdapter implements EventConstant {

		public TraderAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = getActivity().getLayoutInflater().inflate(R.layout.trader_tab_list_item, parent, false);
			return view;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = super.getView(position, convertView, parent);

			if (position % 2 == 0)
				convertView.setBackgroundResource(R.drawable.list_bg_odd);
			else
				convertView.setBackgroundResource(R.drawable.list_bg);

			return convertView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			String traderId = cursor.getString(cursor.getColumnIndex(TraderEntity._ID));
			if (traderId == null) {
				return;
			}

			TextView textViewTraderName = (TextView) view.findViewById(R.id.tvwTraderName);
			textViewTraderName.setText(cursor.getString(cursor.getColumnIndex(TraderEntity.NAME)));

			TextView tvwTraderWebsite = (TextView) view.findViewById(R.id.tvwTraderWebsite);
			tvwTraderWebsite.setText(cursor.getString(cursor.getColumnIndex(TraderEntity.ADDRESS)));

			boolean isFavorite = cursor.getInt(cursor.getColumnIndex(TraderEntity.ISFAVORITE)) == 1;
			ImageView chbFavorite = (ImageView) view.findViewById(R.id.chbFavorite);

			if (isFavorite)
				chbFavorite.setImageResource(android.R.drawable.btn_star_big_on);
			else
				chbFavorite.setImageResource(android.R.drawable.btn_star);
		}
	}

}
