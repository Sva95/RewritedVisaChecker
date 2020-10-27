package com.example.rewritedvisachecker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText edtAppNum, edtAppNumFak;
    private TextView textVisaStatus;
    private Button btnResult;
    private WebView webView;

    private boolean flagOnlyFirstRequest = true;
    private String[] string_type = new String[1];
    private String[] string_year = new String[1];
    private Spinner type_vysa;
    private Spinner year_vysa;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initWebView();
    }

    private void initViews() {
        edtAppNum = findViewById(R.id.edt_app_number);
        edtAppNumFak = findViewById(R.id.edt_number_fake);
        webView = findViewById(R.id.webview);
        textVisaStatus = findViewById(R.id.txt_result_visa);

        btnResult = findViewById(R.id.btn_result);
        btnResult.setOnClickListener(view -> {
            loadBaseUrl();
            submitResult();
        });

        type_vysa = findViewById(R.id.spin_type_visa);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, VisaUtil.VISA_TYPES);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_vysa.setAdapter(aa);
        type_vysa.setOnItemSelectedListener(new VisaTypesSpinnerClass());

        year_vysa = findViewById(R.id.spin_year_visa);
        ArrayAdapter<String> aa2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, VisaUtil.VISA_YEARS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_vysa.setAdapter(aa2);
        year_vysa.setOnItemSelectedListener(new YearsSpinnerClass());
    }

    private void initWebView() {
        webView = findViewById(R.id.webview);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("https://frs.gov.cz/ioff/application-status");
    }

    private void loadUserValidData() {
        loadBaseUrl();
        if (flagOnlyFirstRequest) {
            flagOnlyFirstRequest = false;
            submitResult();
        }
    }

    private void loadBaseUrl() {
        webView.loadUrl("javascript:document.getElementById('edit-ioff-application-number').value = '" + edtAppNum.getText().toString() + "';void(0);");
        webView.loadUrl("javascript:document.getElementById('edit-ioff-application-number-fake').value = '" + edtAppNumFak.getText().toString() + "';void(0);");
        webView.loadUrl("javascript:document.getElementById('edit-ioff-application-code').value = '" + string_type[0] + "';void(0);");
        webView.loadUrl("javascript:document.getElementById('edit-ioff-application-year').value = '" + string_year[0] + "';void(0);");
        webView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByClassName('alert alert-success')[0].innerText);");
    }

    private void submitResult() {
        new Handler().postDelayed(() -> webView.evaluateJavascript("javascript:document.getElementById('edit-submit-button').click()", (ValueCallback) o -> {
        }), 250);
    }


    class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            // handleNetworkError();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //  handleNetworkError();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished");
            loadUserValidData();
        }
    }

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(final String html) {
            Log.d(TAG, "processHTML");

            webView.post(() -> {
                String status = html.substring(html.indexOf(".Status") + 1, html.indexOf("There"));
                textVisaStatus.setText(status);
            });
        }
    }

    class VisaTypesSpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            string_type[0] = VisaUtil.VISA_TYPES[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class YearsSpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            string_year[0] = VisaUtil.VISA_YEARS[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}