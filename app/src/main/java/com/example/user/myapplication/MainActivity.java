package com.example.user.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private hotelArrayAdapter adapter = null;

    private static final int LIST_hotel = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_hotel: {
                    List<hotel> hotels = (List<hotel>)msg.obj;
                    refreshPetList(hotels);
                    break;
                }
            }
        }
    };

    private void refreshPetList(List<hotel> hotels) {
        adapter.clear();
        adapter.addAll(hotels);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvPets = (ListView)findViewById(R.id.listview_hotel);
        hotelArrayAdapter adapter = new hotelArrayAdapter(this, new ArrayList<hotel>());
        lvPets.setAdapter(adapter);


        getPetsFromFirebase();
    }
    class FirebaseThread extends Thread {
        private DataSnapshot dataSnapshot;
        public FirebaseThread(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }
        @Override
        public void run() {
            List<hotel> hotelList = new ArrayList<>();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                DataSnapshot dsAddress = ds.child("Add");
                DataSnapshot dsName = ds.child("Name");
                DataSnapshot dsWebsite = ds.child("Website");
                String address = (String) dsAddress.getValue();
                String name = (String) dsName.getValue();
                String website = (String) dsWebsite.getValue();

                hotel hotel = new hotel();
                hotel.setAddress(address);
                hotel.setName(name);
                hotel.setWeb(website);
                hotelList.add(hotel);
                Log.v("AdoptPet", address + ";" + name + " : " + website);

                Message msg = new Message();
                msg.what = LIST_hotel;
                msg.obj = hotelList;
                handler.sendMessage(msg);
            }
        }


    }
    class hotelArrayAdapter extends ArrayAdapter<hotel> {
        Context context;
        public hotelArrayAdapter(Context context, List<hotel> items) {
            super(context, 0, items);
            this.context = context;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;
            if (convertView == null) {
                itemlayout = (LinearLayout) inflater.inflate(R.layout.hotel_item, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }
            hotel item = (hotel) getItem(position);
            TextView tv_address = (TextView) itemlayout.findViewById(R.id.tv_address);
            tv_address.setText(item.getAddress());
            TextView tv_address = (TextView) itemlayout.findViewById(R.id.tv_name);
            tv_address.setText(item.getName());
            TextView tv_web = (TextView) itemlayout.findViewById(R.id.tv_web);
            tv_web.setText(item.getWeb());
            return itemlayout;
        }
    }
    private void getPetsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new FirebaseThread(dataSnapshot).start();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("AdoptPet", databaseError.getMessage());
            }
        });
    }

}


