package com.example.queuefree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


public class WindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mycontx;

    public WindowAdapter(Context context) {
        mycontx = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);

    }
    private void rendoWindowText(Marker marker,View view){
        String title=marker.getTitle();
        TextView tvTitle=(TextView) view.findViewById(R.id.title);

        if(!title.equals("")){
            tvTitle.setText(title);
        }
        String snippet= marker.getSnippet();
        TextView tvSnippet=(TextView) view.findViewById(R.id.snippet);

        if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }


    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendoWindowText(marker,mWindow);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendoWindowText(marker,mWindow);
        return null;
    }
}
