package com.example.delahuertaalamesa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delahuertaalamesa.databinding.ActivityMainBinding;
import com.example.delahuertaalamesa.propertiesproducts.PropertiesProducts;
import com.example.delahuertaalamesa.recyclerviewMainActivity.ListAdapterMainActivity;
import com.example.delahuertaalamesa.recyclerviewMainActivity.ListProductsMainActivity;
import com.example.delahuertaalamesa.register.Login;
import com.example.delahuertaalamesa.sortViewsProducts.SortViewsProducts;
import com.example.delahuertaalamesa.tools.ItemClickSupport;
import com.example.delahuertaalamesa.tools.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private List<ListProductsMainActivity> productsMonth;
    private ListAdapterMainActivity listAdapterMainActivity;
    private RecyclerView recyclerView;
    private int indexmonth;
//    private RequestQueue requestQueue;
    private String channel;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // hide buttons
        // View decorView = getWindow().getDecorView();
        // int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        // decorView.setSystemUiVisibility(uiOptions);

        try {
            // load components
            getMonth_LoadSpinner();
            loadMenuToolBar();

            // declare the menu buttons for onClick()
            binding.imgSortFuits.setOnClickListener(this);
            binding.imgSortVegetables.setOnClickListener(this);
            binding.imgSortFavorites.setOnClickListener(this);

            // gets the selected cardView from the recyclerView and sends the id_product to PropertiesProducts
            RecyclerView context = findViewById(R.id.recyclerView);
            ItemClickSupport.addTo(context).setOnItemClickListener((recyclerView, position, v) -> {

                int id = productsMonth.get(position).getId_product();
                Intent intent = new Intent(MainActivity.this, PropertiesProducts.class);
                intent.putExtra("id", id);
                startActivity(intent);

            });

            // receive the string with the classification fruits/vegetables/favorites
            Bundle extras = getIntent().getExtras();
            channel = extras.getString("channel");
            //if received from login shows alertGifFavorite
//            if (channel.equalsIgnoreCase("login")) alertGifFavorite();

        } catch (Exception e) {
            Log.d("canalERROR", "Se ha producido una excepción genérica");
            Log.d("canalERROR", Util.PrintEx(e));
        }
    }

    /**
     * Starts arrayList and queries the products of the selected month in the spinner,
     * to load the reciblerView,
     * by default it shows the products of the current month
     */
//    private void loadProducts() {
//        try {
//            productsMonth = new ArrayList<>();
//
//            loadRecycler();
//
//            requestQueue = Volley.newRequestQueue(this);
//            JsonArrayRequest jsonArrayRequest = getProductsMonthID(indexmonth);
//            requestQueue.add(jsonArrayRequest);
//        } catch (Exception e) {
//            Log.d("canalERROR", "Se ha producido una excepción genérica");
//            Log.d("canalERROR", Util.PrintEx(e));
//        }
//    }

    /**
     * LOCAL
     */
    private void loadProducts() {
        try {
            productsMonth = new ArrayList<>();

            // 1. Load Season Data (months for each product)
            Map<String, List<String>> productSeasons = loadSeasons();
            if (productSeasons == null) {
                Log.e("canalERROR", "Error loading season data.");
                return; // Or handle the error as needed
            }

            // 2. Load Product Data
            String json = loadJSONFromAsset("products.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);
                String id_product_str = product.getString("id_product"); // Get product ID as String

                // 3. Check if product is in the selected month
                if (productSeasons.containsKey(id_product_str)) {
                    List<String> months = productSeasons.get(id_product_str);
                    assert months != null;
                    if (months.contains(String.valueOf(indexmonth))) { // Check if the selected month is in the product's season
                        int id_product = Integer.parseInt(id_product_str); // Convert to int only when needed
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

                        productsMonth.add(element);
                    }
                } else {
                    Log.w("canalERROR", "Product " + id_product_str + " not found in season data.");
                }
            }

            loadRecycler();

        } catch (Exception e) {
            Log.e("canalERROR", "Error loading products: " + e.getMessage());
            Log.e("canalERROR", Util.PrintEx(e)); // Assuming Util.PrintEx exists
        }
    }

    private Map<String, List<String>> loadSeasons() {
        Map<String, List<String>> productSeasons = new HashMap<>();
        try {
            String json = loadJSONFromAsset("season.json");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject seasonData = jsonArray.getJSONObject(i);
                String productId = seasonData.getString("id_product");
                JSONArray monthArray = seasonData.getJSONArray("id_month");
                List<String> months = new ArrayList<>();
                for (int j = 0; j < monthArray.length(); j++) {
                    months.add(monthArray.getString(j));
                }
                productSeasons.put(productId, months);
            }
            return productSeasons;

        } catch (Exception e) {
            Log.e("canalERROR", "Error loading seasons from " + "season.json" + ": " + e.getMessage());
            return null;
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
        } catch (IOException ex) {
            Log.e("canalERROR", "Error loading JSON from assets: " + ex.getMessage());
            return null;
        }
        return json;
    }

    /**
     * Load recyclerView
     */
    private void loadRecycler() {
        try {
            listAdapterMainActivity = new ListAdapterMainActivity(productsMonth, this);
            recyclerView = findViewById(R.id.recyclerView);
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
     * Load the spinner with the months of the year and select the current month,
     * passing the month loads the recyclerView products,
     * by default loads the current month
     *
     * @return
     */
    private void getMonth_LoadSpinner() {
        // monthsList is a list containing the names of the months
        List<String> monthsList = new ArrayList<>();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            monthsList.add(months[i]);
        }
        // creation of adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthsList);

        // style
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

        // attach adapter data to the spinner
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        String monthCurrent = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        // get the current month
        indexmonth = 0;
        for (int i = 0; i < monthsList.size(); i++) {
            // compares the current month with the texts displayed in the spinner
            if (monthCurrent.equals(String.valueOf(adapter.getItem(i)))) {
                // gets the index of the match
                indexmonth = i;
            }

        }
        // moves spinner to position
        spinner.setSelection(indexmonth);

        // *** puts the products of the selected month on the spinner
        // SPINNER WITH A NORMAL ONCLICK IS NOT VALID
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexmonth = spinner.getSelectedItemPosition() + 1;
                //calls the method to load the month's products in the recyclerView
                loadProducts();
            }

            // predefined method in case nothing is selected,
            // in our case we always have something loaded, 
            // it cannot be deleted.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Make the query with the server,
     * get the months of the month by passing the id_month
     *
     * @param id_month
     * @return
     */
//    private JsonArrayRequest getProductsMonthID(int id_month) {
//        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/products/ProductsMonth.php?id_month=" + id_month,
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            int length = response.length();
//                            for (int i = 0; i < length; i++) {
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
//                                productsMonth.add(element);
//                                listAdapterMainActivity.notifyItemInserted(productsMonth.size());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                error -> Log.d("canalError", "Error Respuesta en JSON: " + error.getMessage())
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
     * If you login show gifFavorites in alert form
     * Web View is a view of a web page is the solution I found to show the gif in the alert.
     * The only bad thing is that it shakes sometimes I don't know why.
     */
    private void alertGifFavorite() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Ahora puedes guardar tus productos favoritos");

        WebView wv = new WebView(this);
        wv.loadDataWithBaseURL("",
                "<img style=\"max-width: 100%;\" src=\"https://granped.es/webhuertamesa/wp-content/uploads/2023/05/gifFavorite.gif\">",
                "text/html", "utf-8", null);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Aceptar", (dialog, id) -> dialog.dismiss());
        alert.show();
    }
}