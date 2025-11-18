package com.example.prv.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.prv.R;
import com.example.prv.model.Car;
import com.google.android.material.card.MaterialCardView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private Context context;
    private List<Car> cars;

    public CarAdapter(Context context, List<Car> cars) {
        this.context = context;
        this.cars = cars;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return cars != null ? cars.size() : 0;
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView; // Изменили тип
        private final TextView textViewModel;
        private final TextView textViewYear;
        private final TextView textViewPrice;
        private final TextView textViewStatus;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView); // Теперь это CardView
            textViewModel = itemView.findViewById(R.id.textViewModel);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(Car car) {
            // Безопасное обновление текстовых элементов
            if (textViewModel != null) {
                textViewModel.setText(car.getModel());
            }

            if (textViewYear != null) {
                textViewYear.setText(String.valueOf(car.getYear()));
            }

            if (textViewPrice != null) {
                String formattedPrice = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru"))
                        .format(car.getPrice()) + " ₽/день";
                textViewPrice.setText(formattedPrice);
            }

            if (textViewStatus != null) {
                textViewStatus.setText(car.getStatus());
                textViewStatus.setBackgroundColor(
                        "Доступен".equals(car.getStatus()) ?
                                itemView.getContext().getColor(R.color.green_100) :
                                itemView.getContext().getColor(R.color.red_100)
                );
            }

            // Обработчик клика
            if (cardView != null) {
                cardView.setOnClickListener(v -> showCarDetails(car));
            }
        }

        private void showCarDetails(Car car) {
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_car_details, null);

            // Безопасная инициализация элементов диалога (без изображения)
            TextView modelName = dialogView.findViewById(R.id.textDialogTitle);
            TextView year = dialogView.findViewById(R.id.textViewYear);
            TextView price = dialogView.findViewById(R.id.textViewPrice);
            TextView status = dialogView.findViewById(R.id.textViewStatus);

            if (modelName != null) modelName.setText(car.getModel());
            if (year != null) year.setText(String.valueOf(car.getYear()));

            if (price != null) {
                String formattedPrice = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru"))
                        .format(car.getPrice()) + " ₽/день";
                price.setText(formattedPrice);
            }

            if (status != null) {
                status.setText(car.getStatus());
                status.setBackgroundColor(
                        "Доступен".equals(car.getStatus()) ?
                                itemView.getContext().getColor(R.color.green_500) :
                                itemView.getContext().getColor(R.color.red_500)
                );
            }

            new AlertDialog.Builder(itemView.getContext())
                    .setView(dialogView)
                    .setTitle("Детали автомобиля")
                    .setPositiveButton("Забронировать", (d, which) -> {
                        Toast.makeText(itemView.getContext(),
                                "Бронирование " + car.getModel() + " началось!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Закрыть", null)
                    .show();
        }
    }
}