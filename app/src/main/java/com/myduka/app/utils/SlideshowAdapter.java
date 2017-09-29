/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.myduka.app.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.myduka.app.R;

import java.util.List;


/**
 * Created by Brayo on 9/27/2016.
 */
public class SlideshowAdapter extends RecyclerView.Adapter<SlideshowAdapter.SlideshowViewHolder> {

    private List<String> showcase;
    private int rowLayout;
    private Context context;

    public static class SlideshowViewHolder extends RecyclerView.ViewHolder {
        LinearLayout categoryLayout;
        ImageView slideshow_image;

        public SlideshowViewHolder(View v) {
            super(v);
            slideshow_image = (ImageView) v.findViewById(R.id.slideshow_image);
        }
    }

    public SlideshowAdapter(List<String> showcase, int rowLayout, Context context) {
        this.showcase = showcase;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public SlideshowViewHolder onCreateViewHolder(ViewGroup parent,
                                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new SlideshowViewHolder(view);
    }


    @Override
    public void onBindViewHolder(SlideshowViewHolder holder, final int position) {

        switch (position){
            case 0:
                holder.slideshow_image.setImageDrawable(context.getResources().getDrawable(R.drawable.fruits_a));
                break;
            case 1:
                holder.slideshow_image.setImageDrawable(context.getResources().getDrawable(R.drawable.fruits_b));
                break;
            case 2:
                holder.slideshow_image.setImageDrawable(context.getResources().getDrawable(R.drawable.fruits_c));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return showcase.size();
    }
}