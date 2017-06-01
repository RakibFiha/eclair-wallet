package fr.acinq.eclair.swordfish.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import fr.acinq.eclair.payment.PaymentRequest;
import fr.acinq.eclair.swordfish.R;
import fr.acinq.eclair.swordfish.adapters.PaymentListItemAdapter;
import fr.acinq.eclair.swordfish.model.Payment;

public class HomeActivity extends AppCompatActivity {

  public static final String EXTRA_PAYMENTREQUEST = "fr.acinq.eclair.swordfish.PAYMENT_REQUEST";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(false);

    // fetching payments from database
    PaymentListItemAdapter adapter = new PaymentListItemAdapter(this, Payment.findWithQuery(Payment.class, "SELECT * FROM Payment ORDER BY created DESC LIMIT 20"));
    ListView listView = (ListView) findViewById(R.id.main__listview_payments);
    listView.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_home, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_home_channelslist:
        Intent intent = new Intent(this, ChannelsListActivity.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void channel__openScan(View view) {
    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.setOrientationLocked(false);
    integrator.setCaptureActivity(ScanActivity.class);
    integrator.setBeepEnabled(false);
    integrator.initiateScan();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("PaymentActivity", "Got a Result Activity with code " + requestCode + "/" + resultCode);
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null /*&& requestCode == */ && resultCode == RESULT_OK) {
      if (result.getContents() == null) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
      } else {
        try {
          // read content to check if the PR is valid
          PaymentRequest extract = PaymentRequest.read(result.getContents());
          Intent intent = new Intent(this, PaymentActivity.class);
          intent.putExtra(EXTRA_PAYMENTREQUEST, PaymentRequest.write(extract));
          startActivity(intent);
        } catch (Throwable t) {
          Toast.makeText(this, "Invalid Payment Request", Toast.LENGTH_SHORT).show();
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

//  public void channel_goToChannelsList(View view) {
//    Intent intent = new Intent(this, ChannelsListActivity.class);
//    startActivity(intent);
//  }
//
//  public void channel_goToFund(View view) {
//    Intent intent = new Intent(this, FundActivity.class);
//    startActivity(intent);
//  }

}