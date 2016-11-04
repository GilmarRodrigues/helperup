package br.com.alimentar.alergia.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.ItemAdapter;
import br.com.alimentar.alergia.utils.SDCardUtils;

import static android.R.attr.data;

/**
 * Created by gilmar on 24/10/16.
 */
public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private static final int CAMERA_REQUEST = 1;
    private static final int GALERRY_REQUEST = 2;
    private Callback callback;
    private boolean galley = true;
    private File file = null;

    public static interface Callback {
        public void onBottomSheet(Uri imagem, boolean flag);
    }

    public static void show(FragmentManager fm, Callback callback) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("bottom_sheet");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        CustomBottomSheetDialogFragment frag = new CustomBottomSheetDialogFragment();
        frag.callback = callback;
        frag.show(ft, "bottom_sheet");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container);

        ItemAdapter adapter = new ItemAdapter(getActivity());

        ListView lv = (ListView) view.findViewById(R.id.lv_itens);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(onItemClick());

        return (view);
    }

    private AdapterView.OnItemClickListener onItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 123);
                    } else {
                        file = SDCardUtils.getPrivateFile(getActivity().getBaseContext(), System.currentTimeMillis() + ".jpg", Environment.DIRECTORY_PICTURES);
                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(intentCamera, CAMERA_REQUEST);
                    }

                } else {
                    if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            && (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 124);
                    } else {
                        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                        intentGallery.setType("image/*");
                        startActivityForResult(intentGallery, GALERRY_REQUEST);
                    }
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imagem = null;
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (file != null) {
                imagem = Uri.fromFile(file);
            }
        } else if (requestCode == GALERRY_REQUEST && resultCode == Activity.RESULT_OK) {
            imagem = data.getData();
        }

        if (callback != null) {
            callback.onBottomSheet(imagem, galley);
        }
        dismiss();
    }
}
