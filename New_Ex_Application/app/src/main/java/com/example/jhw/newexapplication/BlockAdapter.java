package com.example.jhw.newexapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.CustomViewHolder> {
    // TrainBlockInfo의 정보를 받아 리스트로 연결해주는 클래스

    private ArrayList<TrainBlockInfo> mList = null;
    private Activity context = null;
    private String IP_ADDRESS;

    public BlockAdapter(Activity context, ArrayList<TrainBlockInfo> list,String ip) {
        this.context = context;
        this.mList = list;
        this.IP_ADDRESS = ip;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder { // 리스트(뷰 홀더)에 들어갈 요소 설정
        protected TextView blockIndex;
        protected TextView emptySeat;
        protected RelativeLayout showDetail;
        private String trafficNum;
        private int blockNum;

        public CustomViewHolder(View view) {
            super(view);
            this.blockIndex = (TextView) view.findViewById(R.id.block_index);
            this.emptySeat = (TextView) view.findViewById(R.id.block_seat);
            this.showDetail = (RelativeLayout) view.findViewById(R.id.show_detail);
        }
    }

    @NonNull
    @Override
    public BlockAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { // 리사이클러뷰에 들어갈 뷰홀더를 할당
        // 레이아웃을 뷰로 받아와 그 뷰를 인자로 뷰홀더 생성
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seat_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BlockAdapter.CustomViewHolder viewholder, int i) { // 각 뷰 홀더에 데이터를 연결
        // TrainBlockInfo를 통해 차량 번호, 좌석 정보를 얻고 뷰 홀더의 위치를 통해 열차 칸 번호를 획득
        viewholder.emptySeat.setText(mList.get(i).getEmpty_seat()+"/"+mList.get(i).getTotal_seat());
        viewholder.blockNum = i;
        viewholder.blockIndex.setText(i+1+"호차");
        viewholder.trafficNum = mList.get(i).getTraffic_num();
        viewholder.showDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뷰 홀더 클릭시
                // 칸 번호와 차량 번호를 각각 BlockNumNM, trafficNum을 키로 인텐트를 통해 다음 클래스에 전달
                Intent intent = new Intent(view.getContext(), SeatDetailActivity.class);
                intent.putExtra("BlockNum", viewholder.blockNum);
                intent.putExtra("trafficNum", viewholder.trafficNum);
                view.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() { // 리스트(뷰 홀더) 개수 반환
        return (null != mList ? mList.size() : 0);
    }
}
