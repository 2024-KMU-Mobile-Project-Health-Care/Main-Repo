package com.example.healthcareproject.aiModel;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthcareproject.R;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AiFragment extends Fragment {
    private Map<String, String> painElements;
    private String contextmenuPainPoint;

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

                    registerForContextMenu(button);
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

    // 컨텍스트 메뉴 생성 시
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = requireActivity().getMenuInflater();
        Button button = (Button)v;
        contextmenuPainPoint = button.getText().toString();
        inflater.inflate(R.menu.menu_ai_summation, menu);
    }


    // 컨텍스트 메뉴 버튼 선택 시
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int buttonId = item.getItemId();
        if(buttonId == R.id.menu_btn_save) {
            // 증상 요약 정보 받아오기
            String summationText = painElements.get(contextmenuPainPoint);

            if(contextmenuPainPoint != null && summationText != null) {
                saveTextFileToExternalStorage(contextmenuPainPoint, summationText);
            }
            else{
                Toast.makeText(getContext(), "요약 정보가 없습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if(buttonId == R.id.menu_btn_copy) {
            try {
                // 증상 요약 정보 받아오기
                String summationText = painElements.get(contextmenuPainPoint);

                // 클립보드 복사를 위한 클립보드 매니저 생성
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                // 클립 데이터 생성
                ClipData clip = ClipData.newPlainText("PainSummation", summationText);
                // 클립보드에 데이터 복사
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "요약 내용이 복사되었습니다!", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e){
                Log.e("ContextMenu", "Error : " + e.getMessage());
                Toast.makeText(getContext(), "에러가 발생했습니다! 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }


    public void saveTextFileToExternalStorage(String name, String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd");
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String time = sdf.format(date);
        String fileName = name + time;
        String fileContent = text;
        String folderName = "HealthCareApplication";

        // 안드로이드 API 10 이상일 때
        // MediaStore API 사용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                // 새 컨텐츠 생성 및 제목, 내용, 주소 작성
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName + ".txt");
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + folderName);

                // 파일 저장 주소 설정
                Uri externalUri = MediaStore.Files.getContentUri("external");
                Uri fileUri = getActivity().getContentResolver().insert(externalUri, values);

                if (fileUri != null) {
                    OutputStream outputStream = getActivity().getContentResolver().openOutputStream(fileUri);
                    outputStream.write(fileContent.getBytes());
                    outputStream.close();

                    Toast.makeText(getContext(), "증상 요약 파일이 저장되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "파일 저장 중 URL 생성을 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Log.e("FileExternalSave", "Error : " + e.getMessage());
                Toast.makeText(getContext(), "파일 저장 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
