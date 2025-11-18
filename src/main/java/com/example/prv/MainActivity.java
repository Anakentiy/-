package com.example.prv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prv.activities.LoginActivity;
import com.example.prv.adapter.CarAdapter;
import com.example.prv.model.Car;
import com.example.prv.network.SupabaseConnection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private CarAdapter adapter;
    private List<Car> carList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView textViewError;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            recyclerView = findViewById(R.id.recyclerView);
            progressBar = findViewById(R.id.progressBar);
            textViewError = findViewById(R.id.textViewError);
            Button buttonLogout = findViewById(R.id.buttonLogout);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CarAdapter(this, carList);
            recyclerView.setAdapter(adapter);

            // Обработчик выхода
            buttonLogout.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            });

            loadCarsData();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации UI: " + e.getMessage());
            e.printStackTrace();
            showFatalError("Ошибка инициализации интерфейса. Перезапустите приложение.");
        }
    }

    private void loadCarsData() {
        // Показываем прогресс
        updateUI(() -> {
            progressBar.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        });

        new Thread(() -> {
            try {
                String jsonData = SupabaseConnection.getCarsData();

                if (jsonData != null && !jsonData.isEmpty() && !jsonData.equals("[]")) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Car>>() {}.getType();
                    List<Car> cars = gson.fromJson(jsonData, listType);

                    try {
                        cars = gson.fromJson(jsonData, listType);
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка парсинга JSON: " + e.getMessage());
                        e.printStackTrace();
                        // Попробуем очистить данные перед повторной попыткой
                        String cleanData = jsonData.replaceAll("DEFAULT ", "");
                        try {
                            cars = gson.fromJson(cleanData, listType);
                            Log.d(TAG, "Успешно спарсено после очистки");
                        } catch (Exception ex) {
                            Log.e(TAG, "Не удалось спарсить даже после очистки: " + ex.getMessage());
                        }
                    }

                    List<Car> finalCars = cars;
                    updateUI(() -> {
                        if (finalCars != null && !finalCars.isEmpty()) {
                            carList.clear();
                            carList.addAll(finalCars);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Загружено: " + finalCars.size() + " автомобилей", Toast.LENGTH_SHORT).show();
                        } else {
                            showDataError("Пустой ответ от сервера");
                        }
                    });
                } else {
                    updateUI(() -> showDataError("Не удалось загрузить данные"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка загрузки данных: " + e.getMessage());
                e.printStackTrace();
                updateUI(() -> showDataError("Ошибка подключения: " + e.getMessage()));
            }
        }).start();
    }

    private void showDataError(String message) {
        textViewError.setText(message);
        textViewError.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Используются тестовые данные", Toast.LENGTH_SHORT).show();
        loadTestData();
    }

    private void showFatalError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void loadTestData() {
        List<Car> testCars = new ArrayList<>();
        testCars.add(new Car("Hyundai Solaris", 2023, 2500, "Доступен", ""));
        testCars.add(new Car("Kia Rio", 2022, 2200, "Доступен", ""));
        testCars.add(new Car("BMW X5", 2023, 7000, "Занят", ""));
        testCars.add(new Car("Toyota Camry", 2023, 3000, "Доступен", ""));
        testCars.add(new Car("Lada Vesta", 2024, 1800, "Доступен", ""));

        carList.clear();
        carList.addAll(testCars);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void updateUI(Runnable uiUpdate) {
        if (!isFinishing() && !isDestroyed()) {
            mainHandler.post(uiUpdate);
        }
    }
}