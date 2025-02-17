package com.example.delahuertaalamesa.propertiesproducts;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.delahuertaalamesa.MainActivity;
import com.example.delahuertaalamesa.R;
import com.example.delahuertaalamesa.databinding.ActivityPropertiesProductsBinding;
import com.example.delahuertaalamesa.sortViewsProducts.Favorites;
import com.example.delahuertaalamesa.sortViewsProducts.SortViewsProducts;
import com.example.delahuertaalamesa.tools.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PropertiesProducts extends AppCompatActivity implements View.OnClickListener {
    private ActivityPropertiesProductsBinding binding;
    private int id_product;
    //    private int id_user;
    private List<Favorites> favoritesList;
    private List<Integer> favoritesIDList;

    private List<Integer> monthsProduct;
    private String channel;

    //    private RequestQueue requestQueue;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_properties_products);
        // binding
        binding = ActivityPropertiesProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // hide buttons
        // View decorView = getWindow().getDecorView();
        // int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        // decorView.setSystemUiVisibility(uiOptions);

        try {
            // get id_user and id_product
            Bundle extras = getIntent().getExtras();
            id_product = extras.getInt("id");
//            id_user = Login.intent_id_user;

            // load components
            loadMenuToolBar();

            // method call for web service
            getProduct_Months();

            // classification according to the string received fruits/vegetables/favorites
            Bundle bundle = getIntent().getExtras();
            channel = bundle.getString("channel");

            // declare the menu buttons for onClick()
            binding.imgSortReturn.setOnClickListener(this);
            binding.imgSortFuits.setOnClickListener(this);
            binding.imgSortVegetables.setOnClickListener(this);
            binding.imgSortFavorites.setOnClickListener(this);

            // change the layout if the user is logged in
            ImageView on = findViewById(R.id.img_favorites_on);
            ImageView off = findViewById(R.id.img_favorites_off);

//            if (Login.login) {
//                off.setVisibility(View.VISIBLE);
//
//                // method call for web service
//                favoritesUserList = new ArrayList<>();
//                JsonArrayRequest jsonArrayRequest2 = getFavoritesUserID(Login.intent_id_user);
//                requestQueue.add(jsonArrayRequest2);
//            }
//
//            // add product to favorites
//            off.setOnClickListener(v -> {
//                off.setVisibility(View.INVISIBLE);
//                on.setVisibility(View.VISIBLE);
//
//                JsonObjectRequest jsonObjectRequest3 = addFavorite(new Favorites(id_product, id_user));
//                requestQueue.add(jsonObjectRequest3);
//
//                if (toast != null) toast.cancel();
//                toast = Toast.makeText(PropertiesProducts.this, "Añadido a favoritos", Toast.LENGTH_SHORT);
//                toast.show();
//            });
//
//            // delete product from favorite
//            on.setOnClickListener(v -> {
//                off.setVisibility(View.VISIBLE);
//                on.setVisibility(View.INVISIBLE);
//
//                JsonObjectRequest jsonObjectRequest2 = deleteFavoriteProductID(id_product, id_user);
//                requestQueue.add(jsonObjectRequest2);
//
//                if (toast != null) toast.cancel();
//                toast = Toast.makeText(PropertiesProducts.this, "Eliminado de favoritos", Toast.LENGTH_SHORT);
//                toast.show();
//            });
            /**
             * LOCAL favorites
             */
//            copyAssetToFile("_favorites.json", "_favorites.json");
            off.setVisibility(View.VISIBLE);


            JSONArray favorites = loadFavorites();
            for (int i = 0; i < favorites.length(); i++) {
                favorite(id_product,favorites.getJSONObject(i).getInt("id_product"));
            }

            // add product to favorites
            off.setOnClickListener(v -> {
                off.setVisibility(View.INVISIBLE);
                on.setVisibility(View.VISIBLE);

                addFavorite(id_product);

                if (toast != null) toast.cancel();
                toast = Toast.makeText(PropertiesProducts.this, "Añadido a favoritos", Toast.LENGTH_SHORT);
                toast.show();
            });

            // delete product from favorite
            on.setOnClickListener(v -> {
                off.setVisibility(View.VISIBLE);
                on.setVisibility(View.INVISIBLE);

                deleteFavorite(id_product);

                if (toast != null) toast.cancel();
                toast = Toast.makeText(PropertiesProducts.this, "Eliminado de favoritos", Toast.LENGTH_SHORT);
                toast.show();
            });


        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Performs the query to the web service
     */
    private void getProduct_Months() {
//        requestQueue = Volley.newRequestQueue(this);
//
//        JsonObjectRequest jsonObjectRequest = getProductID(id_product);
//        requestQueue.add(jsonObjectRequest);
        getProductID(id_product);

//        monthsProduct = new ArrayList<>();
//        JsonArrayRequest jsonArrayRequest = getMonthsProductID(id_product);
//        requestQueue.add(jsonArrayRequest);
        getMonthsProductID(id_product);
    }

    /**
     * Gets the product id and prints it in PropertiesProducts view
     */
    private void getProduct(String name_picture, String name_product, String submit, String properties, String production, String curiosities) {
        ImageView imageView = findViewById(R.id.img_content_PP_head);
        TextView tvCommonName = findViewById(R.id.tv_commonname);
        TextView tvSubmit = findViewById(R.id.tv_submit);
        TextView tvProperties = findViewById(R.id.tv_properties);
        TextView tvProduction = findViewById(R.id.tv_production);
        TextView tvCuriosities = findViewById(R.id.tv_curiosities);

        try {
            // glide is an library that makes it easy to place images from a url
            Glide
                    .with(this)
                    .load(getResources().getIdentifier(name_picture, "drawable", getApplicationContext().getPackageName()))
                    .centerInside()
                    .into(imageView);
            //.fitCenter()
            //.centerInside()
            //.centerCrop()

            tvCommonName.setText(name_product);
            tvSubmit.setText(submit);
            tvProperties.setText(properties);
            tvProduction.setText(production);
            tvCuriosities.setText(curiosities);

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);//hide title to menuToolbar
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
//                if (Login.login) {
//                    Intent intent = new Intent(PropertiesProducts.this, SortViewsProducts.class);
//                    intent.putExtra("channel", "favorites");
//                    startActivity(intent);
//                } else {
//                    if (toast != null) toast.cancel();
//                    toast = Toast.makeText(this, "Registro necesario", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
                Intent intent = new Intent(PropertiesProducts.this, SortViewsProducts.class);
                intent.putExtra("channel", "favorites");
                startActivity(intent);
                break;
            }
        }
    }


    /**
     * LOCAL read file
     */
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
     * Performs the query to the web service,
     * the months that a product is consumed
     */
//    private JsonArrayRequest getMonthsProductID(int id_product) {
//        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/months/MonthsProduct.php?id_product=" + id_product,
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            int numContacts = response.length();
//                            for (int i = 0; i < numContacts; i++) {
//                                JSONObject product = response.getJSONObject(i);
//
//                                int id_month = Integer.parseInt(product.getString("id_month"));
//                                String name_month = product.getString("name_month");
//
//                                Months months = new Months(id_month, name_month);
//
//                                monthsProduct.add(months);
//                                season(monthsProduct.get(i).getId_month());
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
     * LOCAL meses_de_un_productID
     */
    private void getMonthsProductID(int id_product) {
        try {
            // Leer el archivo JSON de assets
            String json = loadJSONFromAsset("season.json");
            JSONArray jsonArray = new JSONArray(json);

            // Asegurar que la lista no sea null y limpiarla antes de agregar datos
            if (monthsProduct == null) {
                monthsProduct = new ArrayList<>();
            } else {
                monthsProduct.clear();
            }

            // Buscar todos los meses asociados al producto
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productMonth = jsonArray.getJSONObject(i);

                // Verificar si el id_product coincide
                if (productMonth.getInt("id_product") == id_product) {
                    Object idMonthObject = productMonth.get("id_month");

                    // Si es un JSONArray (lista de meses)
                    if (idMonthObject instanceof JSONArray) {
                        JSONArray monthsArray = (JSONArray) idMonthObject;
                        for (int j = 0; j < monthsArray.length(); j++) {
                            int id_month = monthsArray.getInt(j);
                            monthsProduct.add(id_month);
                        }
                    }
                    // Si es un solo número (en caso de que el JSON tenga otro formato)
                    else if (idMonthObject instanceof Integer) {
                        monthsProduct.add((Integer) idMonthObject);
                    }
                }
            }

            // Llamar a season() después de completar la carga de meses
            for (int id_month : monthsProduct) {
                season(id_month);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("canalERROR", "Error al leer season.json: " + e.getMessage());
        }
    }

    /**
     * Performs the query to the web service,
     * the complete product by passing your id
     */
//    private JsonObjectRequest getProductID(int id_product) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/products/ProductsLogic.php?id_product=" + id_product,
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            String name_picture = response.getString("name_picture");
//                            String name_product = response.getString("name_product");
//                            String submit = response.getString("submit");
//                            String properties = response.getString("properties");
//                            String production = response.getString("production");
//                            String curiosities = response.getString("curiosities");
//
//                            getProduct(name_picture, name_product, submit, properties, production, curiosities);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                error -> Log.d("canalError", "Error Respuesta en JSON: " + error.getMessage())
//        ) {
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                Log.d("VolleyResponseCode", String.valueOf(mStatusCode));
//                return super.parseNetworkResponse(response);
//            }
//        };
//        return jsonObjectRequest;
//    }

    /**
     * LOCAL get_producID
     */

    private void getProductID(int id_product) {
        try {
            // Leer el archivo JSON de assets
            String json = loadJSONFromAsset("products.json");
            JSONArray jsonArray = new JSONArray(json);

            // Buscar el producto con el id_product
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);
                if (product.getInt("id_product") == id_product) {
                    // Obtener los valores
                    String name_picture = product.getString("name_product");
                    String name_product = product.getString("name_product");
                    String submit = product.getString("submit");
                    String properties = product.getString("properties");
                    String production = product.getString("production");
                    String curiosities = product.getString("curiosities");

                    // Llamar a la función que maneja los datos
                    getProduct(name_picture, name_product, submit, properties, production, curiosities);

                    return; // Detener la búsqueda al encontrar el producto
                }
            }

            // Si no se encuentra el producto
            Log.d("ProductSearch", "Producto no encontrado");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * CardView changes color if the product is consumed in that season
     *
     * @param id_month
     */
    private void season(int id_month) {
        try {
            CardView cv_enero = findViewById(R.id.cv_enero);
            CardView cv_febrero = findViewById(R.id.cv_febrero);
            CardView cv_marzo = findViewById(R.id.cv_marzo);
            CardView cv_abril = findViewById(R.id.cv_abril);
            CardView cv_mayo = findViewById(R.id.cv_mayo);
            CardView cv_junio = findViewById(R.id.cv_junio);
            CardView cv_julio = findViewById(R.id.cv_julio);
            CardView cv_agosto = findViewById(R.id.cv_agosto);
            CardView cv_septiembre = findViewById(R.id.cv_septiembre);
            CardView cv_octubre = findViewById(R.id.cv_octubre);
            CardView cv_noviembre = findViewById(R.id.cv_noviembre);
            CardView cv_diciembre = findViewById(R.id.cv_diciembre);

            switch (id_month) {
                case 1:
                    cv_enero.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 2:
                    cv_febrero.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 3:
                    cv_marzo.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 4:
                    cv_abril.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 5:
                    cv_mayo.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 6:
                    cv_junio.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 7:
                    cv_julio.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 8:
                    cv_agosto.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 9:
                    cv_septiembre.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 10:
                    cv_octubre.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 11:
                    cv_noviembre.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
                case 12:
                    cv_diciembre.setCardBackgroundColor(Color.parseColor("#FFBCE1FE"));
                    break;
            }
        } catch (Exception e) {
            Log.d("canalERROR", "Se ha producido una excepción genérica");
            Log.d("canalERROR", Util.PrintEx(e));
        }

    }

    /**
     * Performs the query to the web service,
     * get a user's favorite products
     */
//    private JsonArrayRequest getFavoritesUserID(int id_user) {
//        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                "https://granped.es/huertamesa/favorite/FavoriteLogic.php?id_user=" + id_user,
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                        try {
//                            int numContact = response.length();
//                            for (int i = 0; i < numContact; i++) {
//                                JSONObject product = response.getJSONObject(i);
//
//                                int id_product2 = Integer.parseInt(product.getString("id_product"));
//                                int id_user2 = Integer.parseInt(product.getString("id_user"));
//
//                                Favorites favorite = new Favorites(id_product2, id_user2);
//
//                                favoritesUserList.add(favorite);
//                                favorite(id_product, favoritesUserList.get(i).getId_product());
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
     * Change the img_favorites button
     *
     * @param id_product
     * @param id_product_favorite
     */
    private void favorite(int id_product, int id_product_favorite) {
        ImageView on = findViewById(R.id.img_favorites_on);
        ImageView off = findViewById(R.id.img_favorites_off);

        if (id_product == id_product_favorite) {
            on.setVisibility(View.VISIBLE);
            off.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Performs the query to the web service,
     * add product to favorite,
     * used POST
     */
//    private JsonObjectRequest addFavorite(Favorites favorite) {
//        JSONObject objet = new JSONObject();
//
//        try {
//            objet.put("id_product", favorite.getId_product());
//            objet.put("id_user", favorite.getId_user());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(
//                Request.Method.POST,
//                "https://granped.es/huertamesa/favorite/FavoriteLogic.php",
//                objet,
//                response -> {
//                },
//                error -> Log.d("canalError", "Error Respuesta en JSON: " + error.getMessage())
//        ) {
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                Log.d("VolleyResponseCode", String.valueOf(mStatusCode));
//                return super.parseNetworkResponse(response);
//            }
//        };
//        return jsObjectRequest;
//    }

    /**
     * Performs the query to the web service,
     * delete product to favorite,
     * used DELETE
     */
//    private JsonObjectRequest deleteFavoriteProductID(int id_product, int id_user) {
//        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
//                Request.Method.DELETE,
//                "https://granped.es/huertamesa/favorite/FavoriteLogic.php?id_product=" + id_product + "&id_user=" + id_user,
//                null,
//                response -> {
//                    if (response != null) {
//                        response.toString();
//                    }
//                },
//                error -> Log.d("canalError", "Error Respuesta en JSON: " + error.getMessage())
//        ) {
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                int mStatusCode = response.statusCode;
//                Log.d("VolleyResponseCode", String.valueOf(mStatusCode));
//                return super.parseNetworkResponse(response);
//            }
//        };
//        return jsArrayRequest;
//    }

    /**
     * Save favorites in SharedPreferences
     * Opción sencilla y eficiente para almacenar pequeños datos en formato JSON.
     */
    private JSONArray loadFavorites() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String json = prefs.getString("favorites", "[]"); // Si no hay datos, devuelve un array vacío
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray(); // Devuelve vacío si hay error
        }
    }

    private void saveFavorites(JSONArray favorites) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("favorites", favorites.toString());
        editor.apply(); // Guardar de forma asíncrona
    }

    private void addFavorite(int id_product) {
        try {
            JSONArray favorites = loadFavorites();

            // Verificar si ya existe
            for (int i = 0; i < favorites.length(); i++) {
                if (favorites.getJSONObject(i).getInt("id_product") == id_product) {
                    Log.d("addFavorite", "El producto ya está en favoritos.");
                    return;
                }
            }

            // Agregar nuevo producto
            JSONObject newFavorite = new JSONObject();
            newFavorite.put("id_product", id_product);
            favorites.put(newFavorite);

            // Guardar lista actualizada
            saveFavorites(favorites);
            Log.d("addFavorite", "Producto agregado a favoritos.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteFavorite(int id_product) {
        try {
            JSONArray favorites = loadFavorites();
            JSONArray updatedFavorites = new JSONArray();
            boolean found = false;

            for (int i = 0; i < favorites.length(); i++) {
                if (favorites.getJSONObject(i).getInt("id_product") == id_product) {
                    found = true;
                } else {
                    updatedFavorites.put(favorites.getJSONObject(i));
                }
            }

            if (!found) {
                Log.d("removeFavorite", "El producto no estaba en favoritos.");
                return;
            }

            // Guardar lista actualizada
            saveFavorites(updatedFavorites);
            Log.d("removeFavorite", "Producto eliminado de favoritos.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

