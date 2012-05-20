package org.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.android.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Classe représentant une image dans la Galerie.
 * 
 * @author Elodie
 * 
 */
public class ImageAdapter extends BaseAdapter {
	/** Contexte de l'activité appelante */
	private static Context mContext;

	/** L'arrière plan d'une image dans la galerie */
	private int mImageBackground;

	/** L'album que représente la galerie */
	private ArrayList<String> mPictures;

	/** L'image a représenter */
	private Bitmap mBitmapImage;

	private static int mScreenWidth;
	private static int mScreenHeight;

	/**
	 * Constructeur.
	 * 
	 * @param c
	 *            Le contexte de l'activité appelante.
	 * @param album
	 *            L'album a représenter dans la galerie.
	 */
	public ImageAdapter(Context c, ArrayList<String> pictures) {
		mContext = c;
		mPictures = pictures;
		
		TypedArray ta = mContext
				.obtainStyledAttributes(R.styleable.PictureGallery);
		mImageBackground = ta.getResourceId(
				R.styleable.PictureGallery_android_galleryItemBackground, 1);
		ta.recycle();

		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels - 250;
	}

	/**
	 * @return Le nombre d'éléments dans la galerie.
	 */
	public int getCount() {
		return mPictures.size();
	}

	/**
	 * @return L'élément à la position donnée.
	 */
	public Object getItem(int position) {
		return position;
	}

	/**
	 * @return L'Id de l'objet à la position donnée.
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * La vue à représenter à la position donnée.
	 * 
	 * @param position
	 *            La position à représenter.
	 */
	public View getView(final int position, View arg1, ViewGroup arg2) {
		ImageView iv = new ImageView(mContext);

		try {
			String toDisplay = mPictures.get(position);
			mBitmapImage = decodeFile(new File(toDisplay));

			if (mBitmapImage != null) {
				iv.setImageBitmap(mBitmapImage);

				iv.setScaleType(ImageView.ScaleType.FIT_XY);

				iv.setLayoutParams(new Gallery.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				iv.setBackgroundResource(mImageBackground);
			}
		} catch (IndexOutOfBoundsException iobe) {
			return null;
		}
		return iv;
	}

	/**
	 * 
	 * Fait la correspondance entre le fichier passé en argument et l'image.
	 * Redimensionne l'image pour diminuer la taille utilisée en mémoire par
	 * l'application.
	 * 
	 * @param f
	 *            Le fichier dont on veut tirer une image.
	 * @return L'image représentée par le fichier passé en argument.
	 */
	public static Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;

			int imageHeight = o.outHeight;
			int imageWidth = o.outWidth;

			if (imageHeight > imageWidth) {
				while (o.outHeight / scale >= mScreenHeight) {
					scale *= 2;
				}
			} else {
				while (o.outWidth / scale >= mScreenWidth) {
					scale *= 2;
				}
			}

			o.inJustDecodeBounds = false;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);

			fis.close();
		} catch (IOException e) {
		}
		return b;
	}
}
