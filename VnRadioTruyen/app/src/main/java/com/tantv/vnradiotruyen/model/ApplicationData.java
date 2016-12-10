package com.tantv.vnradiotruyen.model;

import com.tantv.vnradiotruyen.R;

import java.util.ArrayList;

/**
 * Created by tantv on 24/09/2015.
 */
public class ApplicationData {

    public static final String BLOG_RADIO = "MS01_";
    public static final String SACH_NOI = "MS02_";
    public static final String TRUYEN_DEM_KHUYA = "MS03_";
    public static final String TRUYEN_MA_KINH_DI = "MS04_";
    public static final String DASU_KIEMHIEP = "MS05_";
    public static final String COTICH_THIEUNHI = "MS06_";
    public static final String TINHYEU = "MS07_";
    public static final String KHAC = "MS08_";

    public static String sTitle[] = {"Radio Online", "Audio Books", "Truyện Online", "Mở rộng"}; //4
    public static String sRadio[] = {"Blog Radio"}; //1
    public static String sAudioBooks[] = {"Sách nói"}; //1
    public static String sTruyenOnline[] = {"Truyện đêm khuya", "Truyện ma - kinh dị", "Dã sử - kiếm hiệp", "Cổ tích - thiếu nhi", "Tình yêu", "Khác"}; //6
    public static String sMoRong[] = {"Đánh giá", "Ứng dụng khác", "Góp ý", "Giới thiệu"};

    public static ArrayList<MenuObject> addData() {
        ArrayList<MenuObject> menuObject = new ArrayList<>();
        MenuObject object;
        for (int i = 0; i < 16; i++) {
            switch (i) {
                case 0:
                    object = new MenuObject(R.mipmap.icon_menu_1, sTitle[0], 0);
                    menuObject.add(object);
                    break;
                case 2:
                    object = new MenuObject(R.mipmap.icon_menu_1, sTitle[1], 0);
                    menuObject.add(object);
                    break;
                case 4:
                    object = new MenuObject(R.mipmap.icon_menu_1, sTitle[2], 0);
                    menuObject.add(object);
                    break;
                case 11:
                    object = new MenuObject(R.mipmap.icon_menu_1, sTitle[3], 0);
                    menuObject.add(object);
                    break;
                case 1:
                    object = new MenuObject(R.drawable.ic_audio, sRadio[0], 1);
                    menuObject.add(object);
                    break;
                case 3:
                    object = new MenuObject(R.drawable.ic_audio, sAudioBooks[0], 1);
                    menuObject.add(object);
                    break;
                case 5:
                    object = new MenuObject(R.drawable.ic_audio, sTruyenOnline[0], 1);
                    menuObject.add(object);
                    break;
                case 6:
                    object = new MenuObject(R.drawable.ic_audio, sTruyenOnline[1], 1);
                    menuObject.add(object);
                    break;
                case 7:
                    object = new MenuObject(R.drawable.ic_audio, sTruyenOnline[2], 1);
                    menuObject.add(object);
                    break;
                case 8:
                    object = new MenuObject(R.drawable.ic_audio, sTruyenOnline[3], 1);
                    menuObject.add(object);
                    break;
                case 9:
                    object = new MenuObject(R.drawable.ic_audio, sTruyenOnline[4], 1);
                    menuObject.add(object);
                    break;
                case 10:
                    object = new MenuObject(R.drawable.ic_audio, sTruyenOnline[5], 1);
                    menuObject.add(object);
                    break;
                case 12:
                    object = new MenuObject(R.drawable.ic_audio, sMoRong[0], 1);
                    menuObject.add(object);
                    break;
                case 13:
                    object = new MenuObject(R.drawable.ic_audio, sMoRong[1], 1);
                    menuObject.add(object);
                    break;
                case 14:
                    object = new MenuObject(R.drawable.ic_audio, sMoRong[2], 1);
                    menuObject.add(object);
                    break;
                case 15:
                    object = new MenuObject(R.drawable.ic_audio, sMoRong[3], 1);
                    menuObject.add(object);
                    break;
            }
        }
        return menuObject;
    }
}
