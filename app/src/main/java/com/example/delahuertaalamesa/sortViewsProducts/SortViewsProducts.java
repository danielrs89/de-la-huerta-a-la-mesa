package com.example.delahuertaalamesa.sortViewsProducts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delahuertaalamesa.MainActivity;
import com.example.delahuertaalamesa.R;
import com.example.delahuertaalamesa.databinding.ActivitySortViewsProductsBinding;
import com.example.delahuertaalamesa.propertiesproducts.PropertiesProducts;
import com.example.delahuertaalamesa.recyclerviewMainActivity.ListAdapterMainActivity;
import com.example.delahuertaalamesa.recyclerviewMainActivity.ListProductsMainActivity;
import com.example.delahuertaalamesa.register.Login;
import com.example.delahuertaalamesa.tools.ItemClickSupport;
import com.example.delahuertaalamesa.tools.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SortViewsProducts extends AppCompatActivity implements View.OnClickListener {
    private ListAdapterMainActivity listAdapterMainActivity;
    private RecyclerView recyclerView;
    private List<ListProductsMainActivity> fruitsLists;
    private final int FRUITS_SPLIT_VEGETABLES = 49;
    private List<ListProductsMainActivity> vegetablesLists;

    private List<ListProductsMainActivity> favoritesLists;
    private ActivitySortViewsProductsBinding binding;
    private String channel;
//    private RequestQueue requestQueue;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_sort_views_products);
        // binding
        binding = ActivitySortViewsProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // hide buttons
        // View decorView = getWindow().getDecorView();
        // int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        // decorView.setSystemUiVisibility(uiOptions);

        // load components
        loadMenuToolBar();

        // classification according to the string received fruits/vegetables/favorites
        Bundle bundle = getIntent().getExtras();
        channel = bundle.getString("channel");

        switch (channel) {
            case "fruits": {
                binding.tvTitleSort.setText("Frutas");
                fruitsLists = new ArrayList<>();

                loadRecycler();

//                requestQueue = Volley.newRequestQueue(this);
//                JsonArrayRequest jsonArrayRequest = getProductFruits();
//                requestQueue.add(jsonArrayRequest);
                getProductFruits();

                break;
            }
            case "vegetables": {
                binding.tvTitleSort.setText("Verduras");
                vegetablesLists = new ArrayList<>();

                loadRecycler();

//                requestQueue = Volley.newRequestQueue(this);
//                JsonArrayRequest jsonArrayRequest = getProductVegetables();
//                requestQueue.add(jsonArrayRequest);
                getProductVegetables();
                break;
            }
            case "favorites": {
                binding.tvTitleSort.setText("Favoritos");
                favoritesLists = new ArrayList<>();
//                int intent_id_user = Login.intent_id_user;

                loadRecycler();

//                requestQueue = Volley.newRequestQueue(this);
//                JsonArrayRequest jsonArrayRequest = getProductFavorites(intent_id_user);
//                requestQueue.add(jsonArrayRequest);

                break;
            }
        }

        // declare the menu buttons for onClick()
        binding.imgSortReturn.setOnClickListener(this);
        binding.imgSortFuits.setOnClickListener(this);
        binding.imgSortVegetables.setOnClickListener(this);
        binding.imgSortFavorites.setOnClickListener(this);

        // gets the selected cardView from the recyclerView and sends the id_product to PropertiesProducts
        RecyclerView context = findViewById(R.id.rv_content_sortviewproducts);
        ItemClickSupport.addTo(context).setOnItemClickListener((recyclerView, position, v) -> {
            int id = 0;

            if (channel.equalsIgnoreCase("fruits")) {
                id = fruitsLists.get(position).getId_product();
            } else if (channel.equalsIgnoreCase("vegetables")) {
                id = vegetablesLists.get(position).getId_product();
            } else if (channel.equalsIgnoreCase("favorites")) {
                id = favoritesLists.get(position).getId_product();
            }

            Intent intent = new Intent(SortViewsProducts.this, PropertiesProducts.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }

    /**
     * Load recyclerView,
     * depending on the intent received
     */
    private void loadRecycler() {
        try {
            if (channel.equalsIgnoreCase("fruits")) {
                listAdapterMainActivity = new ListAdapterMainActivity(fruitsLists, this);
            } else if (channel.equalsIgnoreCase("vegetables")) {
                listAdapterMainActivity = new ListAdapterMainActivity(vegetablesLists, this);
            } else if (channel.equalsIgnoreCase("favorites")) {
                listAdapterMainActivity = new ListAdapterMainActivity(favoritesLists, this);
            }

            recyclerView = findViewById(R.id.rv_content_sortviewproducts);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapterMainActivity);

        } catch (Exception e) {
            Log.d("canalERROR", "Se ha producido una excepción genérica");
            Log.d("canalERROR", Util.PrintEx(e));
        }
    }

    /**
     * Load menuToolBar
     */
    private void loadMenuToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);// hide title to menuToolbar
    }

    /**
     * Options to menuToolBar
     *
     * @param menu The options menu in which you place your items.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * Selection of menuToolBar options
     * Handle item selection
     *
     * @param item The menu item that was selected.
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.register:
//                Intent intent = new Intent(this, Login.class);
//                startActivity(intent);
                if (toast != null) toast.cancel();
                toast = Toast.makeText(this, "No disponible.", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.contact:
                acceptContact();
                return true;
            case R.id.web:
//                acceptWeb();
                if (toast != null) toast.cancel();
                toast = Toast.makeText(this, "No disponible.", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays an AlertDialog to confirm or cancel,
     * going to WhatsApp channel after pressing contact
     */
    private void acceptContact() {
        AlertDialog alertDialog = new AlertDialog
                .Builder(this)
                .setPositiveButton("Sí, continuar", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/K0CD4DshuaUJGKtSPOMyDD"));
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setTitle("Confirmar")
                .setMessage("¿Quieres ir al canal de WhatsApp?")
                .create();
        alertDialog.show();
    }
    /**
     * Displays an AlertDialog to confirm or cancel,
     * going to web "De la huerta a la mesa" after pressing web
     */
    private void acceptWeb() {
        AlertDialog alertDialog = new AlertDialog
                .Builder(this)
                .setPositiveButton("Sí, continuar", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://granped.es/webhuertamesa"));
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setTitle("Confirmar")
                .setMessage("¿Quieres ir a la web \n\"De la huerta a la mesa\"?")
                .create();
        alertDialog.show();
    }

    /**
     * Collects the options performed by the menuToolBar buttons
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        ImageView imageView = (ImageView) v;
        switch (imageView.getId()) {
            case R.id.img_sortReturn: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.img_sortFuits: {
                Intent intent = new Intent(this, SortViewsProducts.class);
                intent.putExtra("channel", "fruits");
                startActivity(intent);
                break;
            }
            case R.id.img_sortVegetables: {
                Intent intent = new Intent(this, SortViewsProducts.class);
                intent.putExtra("channel", "vegetables");
                startActivity(intent);
                break;
            }
            case R.id.img_sortFavorites: {
                if (Login.login) {
                    Intent intent = new Intent(this, SortViewsProducts.class);
                    intent.putExtra("channel", "favorites");
                    startActivity(intent);
                } else {
                    if (toast != null) toast.cancel();
                    toast = Toast.makeText(this, "Registro necesario", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            }
        }
    }

    /**
     * Make the query with the server,
     * get all fruits
     *
     * @return
     */
//    private JsonArrayRequest getProductFruits() {
//        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/products/ProductsFruits.php",
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            int numContact = response.length();
//                            for (int i = 0; i < numContact; i++) {
//                                JSONObject product = response.getJSONObject(i);
//
//                                int id_product = Integer.parseInt(product.getString("id_product"));
//                                String name_picture = product.getString("name_picture");
//                                String name_product = product.getString("name_product");
//                                String submit = product.getString("submit");
//                                String properties = product.getString("properties");
//                                String production = product.getString("production");
//                                String curiosities = product.getString("curiosities");
//
//                                ListProductsMainActivity element = new ListProductsMainActivity(
//                                        id_product,
//                                        name_picture,
//                                        name_product,
//                                        submit,
//                                        properties,
//                                        production,
//                                        curiosities,
//                                        getResources().getIdentifier(name_picture + "", "drawable", getApplicationContext().getPackageName())
//                                );
//
//                                fruitsLists.add(element);
//                                listAdapterMainActivity.notifyItemInserted(fruitsLists.size());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                error -> Log.d("ErrorVolley", "Error Respuesta en JSON: " + error.getMessage())
//        ) {
//            @Override
//            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                Log.d("VolleyResponseCode", String.valueOf(mStatusCode));
//                return super.parseNetworkResponse(response);
//            }
//        };
//        return jsArrayRequest;
//    }

    /**
     * LOCAL
     */
    private void getProductFruits() {
        try {
            fruitsLists = new ArrayList<>();

            // Leer el archivo JSON de assets
            String json = loadJSONFromAsset("products.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);

                int aux_id_product = product.getInt("id_product");
                if (aux_id_product <= FRUITS_SPLIT_VEGETABLES) { // Filtra entre frutas id<=49 verduras
                    int id_product = product.getInt("id_product");
                    String name_picture = product.getString("name_product");
                    String name_product = product.getString("name_product");
                    String submit = product.getString("submit");
                    String properties = product.getString("properties");
                    String production = product.getString("production");
                    String curiosities = product.getString("curiosities");

                    ListProductsMainActivity element = new ListProductsMainActivity(
                            id_product,
                            name_picture,
                            name_product,
                            submit,
                            properties,
                            production,
                            curiosities,
                            getResources().getIdentifier(name_picture, "drawable", getApplicationContext().getPackageName())
                    );

                    fruitsLists.add(element);
                }
            }

            loadRecycler(); // Cargar RecyclerView con los datos

        } catch (Exception e) {
            Log.d("canalERROR", "Error al cargar productos desde archivo local");
            Log.d("canalERROR", Util.PrintEx(e));
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e("JSON Load Error", "Error al leer el archivo JSON", e);
        }
        return json;
    }

    /**
     * Make the query with the server,
     * get all vegetables
     *
     * @return
     */
//    private JsonArrayRequest getProductVegetables() {
//        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/products/ProductsVegetables.php",
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            int numContact = response.length();
//                            for (int i = 0; i < numContact; i++) {
//                                JSONObject product = response.getJSONObject(i);
//
//                                int id_product = Integer.parseInt(product.getString("id_product"));
//                                String name_picture = product.getString("name_picture");
//                                String name_product = product.getString("name_product");
//                                String submit = product.getString("submit");
//                                String properties = product.getString("properties");
//                                String production = product.getString("production");
//                                String curiosities = product.getString("curiosities");
//
//                                ListProductsMainActivity element = new ListProductsMainActivity(
//                                        id_product,
//                                        name_picture,
//                                        name_product,
//                                        submit,
//                                        properties,
//                                        production,
//                                        curiosities,
//                                        getResources().getIdentifier(name_picture + "", "drawable", getApplicationContext().getPackageName())
//                                );
//
//                                vegetablesLists.add(element);
//                                listAdapterMainActivity.notifyItemInserted(vegetablesLists.size());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                error -> Log.d("ErrorVolley", "Error Respuesta en JSON: " + error.getMessage())
//        ) {
//            @Override
//            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                Log.d("VolleyResponseCode", String.valueOf(mStatusCode));
//                return super.parseNetworkResponse(response);
//            }
//        };
//        return jsArrayRequest;
//    }

    /**
     * LOCAL
     */
    private void getProductVegetables() {
        try {
            vegetablesLists = new ArrayList<>();

            // Leer el archivo JSON de assets
            String json = loadJSONFromAsset("products.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);

                int aux_id_product = product.getInt("id_product");
                if (aux_id_product >= FRUITS_SPLIT_VEGETABLES) {
                    int id_product = product.getInt("id_product");
                    String name_picture = product.getString("name_product");
                    String name_product = product.getString("name_product");
                    String submit = product.getString("submit");
                    String properties = product.getString("properties");
                    String production = product.getString("production");
                    String curiosities = product.getString("curiosities");

                    ListProductsMainActivity element = new ListProductsMainActivity(
                            id_product,
                            name_picture,
                            name_product,
                            submit,
                            properties,
                            production,
                            curiosities,
                            getResources().getIdentifier(name_picture, "drawable", getApplicationContext().getPackageName())
                    );

                    vegetablesLists.add(element);
                }
            }

            loadRecycler(); // Cargar RecyclerView con los datos

        } catch (Exception e) {
            Log.d("canalERROR", "Error al cargar productos desde archivo local");
            Log.d("canalERROR", Util.PrintEx(e));
        }
    }

    /**
     * Make the query with the server,
     * get all products favorites to a user
     *
//     * @param id_user
     * @return
     */
//    private JsonArrayRequest getProductFavorites(int id_user) {
//        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/products/ProductsFavoritesIdUser.php?id_user=" + id_user,
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            int numContact = response.length();
//                            for (int i = 0; i < numContact; i++) {
//                                JSONObject product = response.getJSONObject(i);
//
//                                int id_product = Integer.parseInt(product.getString("id_product"));
//                                String name_picture = product.getString("name_picture");
//                                String name_product = product.getString("name_product");
//                                String submit = product.getString("submit");
//                                String properties = product.getString("properties");
//                                String production = product.getString("production");
//                                String curiosities = product.getString("curiosities");
//
//                                ListProductsMainActivity element = new ListProductsMainActivity(
//                                        id_product,
//                                        name_picture,
//                                        name_product,
//                                        submit,
//                                        properties,
//                                        production,
//                                        curiosities,
//                                        getResources().getIdentifier(name_picture + "", "drawable", getApplicationContext().getPackageName())
//                                );
//
//                                favoritesLists.add(element);
//                                listAdapterMainActivity.notifyItemInserted(favoritesLists.size());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                error -> Log.d("ErrorVolley", "Error Respuesta en JSON: " + error.getMessage())
//        ) {
//            @Override
//            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                Log.d("VolleyResponseCode", String.valueOf(mStatusCode));
//                return super.parseNetworkResponse(response);
//            }
//        };
//        return jsArrayRequest;
//    }
    /**
     * LOCAL favorites
     */







}
