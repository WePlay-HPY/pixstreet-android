package hpy.pixstreet;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Iterator;

import hpy.pixstreet.camera.CameraActivity;

public class OpenStreetMap extends AppCompatActivity {

    private MapDatas mapDatas = MapDatas.getInstance();
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private ItemizedOverlay mMyLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_street_map);

        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(16);
        GeoPoint startPoint = new GeoPoint(49.1833, -0.35);
        mapController.setCenter(startPoint);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        final Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenStreetMap.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        putPoints();

    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }


    public void putPoints(){
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (Iterator<MapItem> i = mapDatas.getDatasMap().iterator(); i.hasNext(); ) {
            double longitude = i.next().getLongitude();
            double latitude  = i.next().getLatitude();
            GeoPoint geo = new GeoPoint(latitude, longitude);
            items.add(new OverlayItem(i.next().getHighScore(), "Play on this point", geo));
        }


        mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index,
                                                     final OverlayItem item) {
                        Toast.makeText(
                                OpenStreetMap.this,
                                "Score : " + item.getTitle(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index,
                                                   final OverlayItem item) {
                        Toast.makeText(
                                OpenStreetMap.this,
                                item.getSnippet(), Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, this.getApplicationContext());
        map.getOverlays().add(this.mMyLocationOverlay);
        map.invalidate();
    }
}
