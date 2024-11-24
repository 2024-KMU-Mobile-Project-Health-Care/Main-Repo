package com.example.healthcareproject.aiModel;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiFragment extends Fragment {
    private Map<String, String> painElements;

    public AiFragment(Map<String, String> inputMap) {
        painElements = inputMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 동적 레이아웃 생성
        TableLayout tableLayout = new TableLayout(getContext());
        tableLayout.setStretchAllColumns(true);

        // 증상 데이터에서 키와 값을 분리
        List<String> keys = new ArrayList<>(painElements.keySet());
        List<String> values = new ArrayList<>(painElements.values());

        // 테이블 행과 열을 설정
        int columnCount = 4;
        int rowCount = (int) Math.ceil((double) keys.size() / columnCount);

        int index = 0;
        for (int i = 0; i < rowCount; i++) {
            TableRow tableRow = new TableRow(getContext());

            for (int j = 0; j < columnCount; j++) {
                if (index < keys.size()) {
                    String element = keys.get(index);
                    String description = values.get(index);

                    // 버튼 생성
                    Button button = new Button(getContext());
                    button.setText(element);
                    button.setGravity(Gravity.CENTER);

                    // 버튼 크기 설정
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    params.setMargins(10, 10, 10, 10);
                    button.setLayoutParams(params);

                    button.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("증상 요약")
                                .setMessage(description)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()); // 닫기 버튼
                        builder.create().show();
                    });

                    tableRow.addView(button);
                    index++;
                } else {
                    View emptyView = new View(getContext());
                    emptyView.setLayoutParams(new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));
                    tableRow.addView(emptyView);
                }
            }
            tableLayout.addView(tableRow);
        }
        return tableLayout;
    }
}
