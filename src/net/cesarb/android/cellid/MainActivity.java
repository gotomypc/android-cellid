package net.cesarb.android.cellid;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.TextView;

public class MainActivity extends Activity {
	private class CellStateListener extends PhoneStateListener {

		private static final int EVENTS = LISTEN_CELL_LOCATION
				| LISTEN_SERVICE_STATE;

		@Override
		public void onCellLocationChanged(CellLocation location) {
			updateCellLocation(location);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			updateServiceState(serviceState.getOperatorNumeric());
		}

	}

	private CellStateListener listener = new CellStateListener();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onPause() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(listener, CellStateListener.LISTEN_NONE);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(listener, CellStateListener.EVENTS);

		updateServiceState(telephonyManager.getNetworkOperator());
		updateCellLocation(telephonyManager.getCellLocation());
	}

	private static GsmCellLocation gsmCellLocation(CellLocation location) {
		try {
			return (GsmCellLocation) location;
		} catch (ClassCastException e) {
			return null;
		}
	}

	private void updateCellLocation(CellLocation location) {
		GsmCellLocation gsmLocation = gsmCellLocation(location);
		setText(R.id.lac, R.string.lac,
				gsmLocation != null ? gsmLocation.getLac() : -1);
		setText(R.id.cid, R.string.cid,
				gsmLocation != null ? gsmLocation.getCid() : -1);
		setText(R.id.psc, R.string.psc,
				gsmLocation != null ? gsmLocation.getPsc() : -1);
	}

	private void updateServiceState(String operator) {
		setText(R.id.mcc_mnc, R.string.mcc_mnc, operator);
	}

	private void setText(int id, int label, String string) {
		TextView view = (TextView) findViewById(id);
		view.setText(getString(label) + ": " + string);
	}

	private void setText(int id, int label, int number) {
		setText(id, label, number >= 0 ? String.valueOf(number) : "");
	}
}
