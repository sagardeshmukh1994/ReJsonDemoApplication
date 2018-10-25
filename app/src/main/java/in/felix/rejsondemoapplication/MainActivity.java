package in.felix.rejsondemoapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    String TAG="MainActivity";

    List<Contact> contacts=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHit = (Button) findViewById(R.id.btnHit);
        txtJson = (TextView) findViewById(R.id.tvJsonItem);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new asyncTaskToGetData().execute("http://api.androidhive.info/contacts/");

                //new asyncTaskToGetData().execute("http://api.androidhive.info/contacts/");
                // new JsonTask().execute("http://api.myjson.com/bins/d5y1e");//http://api.androidhive.info/contacts/");
            }
        });
    }



    private class asyncTaskToGetData extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            String jsonStr=null;
            try {
                //Making a request to url and getting response
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(strings[0]));
                HttpResponse response = client.execute(request);


                jsonStr = EntityUtils.toString(response.getEntity());
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd.isShowing()){
                pd.dismiss();
            }


            try {
                Gson gson = new Gson();
                ContactData contactData= gson.fromJson(s, ContactData.class);


                JSONObject mainObj=new JSONObject(s);
                JSONArray contactArray=new JSONArray();

                contactArray=mainObj.getJSONArray("contacts");


                for(int i=0;i<contactArray.length()-1;i++){

                    JSONObject user=contactArray.getJSONObject(i);
                    Contact contact=new Contact();

                    contact.setId(user.getString("id"));
                    contact.setName(user.getString("name"));
                    contact.setAddress(user.getString("address"));
                    contact.setEmail(user.getString("email"));
                    contact.setGender(user.getString("gender"));

                    JSONObject numbersObj=user.getJSONObject("phone");
                   Phone phone=new Phone();
                   phone.setMobile(numbersObj.getString("mobile"));
                   phone.setHome(numbersObj.getString("home"));
                   phone.setOffice(numbersObj.getString("office"));


                    contact.setPhone(phone);

                    contacts.add(contact);


//                    String name=user.getString("name");
//
//                    JSONObject numbersObj=user.getJSONObject("phone");
//
//                    String moNum=numbersObj.getString("mobile");
//
//
//                    Toast.makeText(getApplicationContext(),"Name="+name+" Number="+moNum,Toast.LENGTH_SHORT).show();


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
