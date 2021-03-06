package hpy.pixstreet;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hpy.pixstreet.models.Node;
import hpy.pixstreet.ws.PixStreetRequestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Joey on 12/01/2017.
 */
public class MapDatas {
    private static MapDatas ourInstance = new MapDatas();
    private List<MapItem> _datas = new ArrayList<>();

    public static MapDatas getInstance() {
        return ourInstance;
    }


    private MapDatas() {
        Call<List<Node>> call = PixStreetRequestClient.get().getNodes();
        call.enqueue(new Callback<List<Node>>() {
            @Override
            public void onFailure(Call<List<Node>> call, Throwable t) {
                Log.d("PixStreet", "Error Occured: " + t.getMessage());
            }

            @Override
            public void onResponse(Call<List<Node>> call, Response<List<Node>> response) {
                Log.d("PixStreet", "Successfully response fetched" );

                List<Node> nodes=response.body();
                if(nodes.size() > 0) {
                    Log.d("PixStreet", "ITEM FETCHED o//");

                    for(Iterator<Node> i = nodes.iterator(); i.hasNext(); ) {
                        Node node = i.next();
                        List<Double> loc = node.getLoc();
                        double longitude = loc.get(0);
                        double latitude  = loc.get(1);
                        Pair<String, Integer> highScore = node.getHighScore();
                        _datas.add(new MapItem(longitude, latitude, highScore.first, highScore.second ));
                    }

                }else{
                    Log.d("PixStreet", "No item found");
                }
            }
        });
    }

    public List<MapItem> getDatasMap(){return _datas;}
}
