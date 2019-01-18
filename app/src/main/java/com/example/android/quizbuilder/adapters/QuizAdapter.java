package com.example.android.quizbuilder.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.database.QuizEntry;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    public interface OnQuizClick {
        void onQuizClick(long quizId);
    }

    private Context mContext;
    private List<QuizEntry> mQuizList;
    private OnQuizClick mListener;
    private long mLastClickTime = 0;

    public QuizAdapter(Context context, OnQuizClick listener) {
        mContext = context;
        mListener = listener;
        mQuizList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.quiz_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date dateObject = mQuizList.get(position).getDate();
        String date = DateFormat.getDateTimeInstance().format(dateObject);
        String name = mQuizList.get(position).getName();
        int questionCount = mQuizList.get(position).getPages().size();

        holder.mDateTv.setText(date);
        holder.mNameTv.setText(name);
        if (questionCount > 0) {
            holder.mQuestionCountTv.setVisibility(View.VISIBLE);
            holder.mQuestionCountTv.setText(mContext.getString(R.string.question_count_args, questionCount));
        } else {
            holder.mQuestionCountTv.setVisibility(View.INVISIBLE);
        }
    }

    public void setData(final List<QuizEntry> newList) {
        if (mQuizList.isEmpty()) {
            mQuizList = newList;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mQuizList.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldPosition, int newPosition) {
                    return mQuizList.get(oldPosition).getId() == newList.get(newPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    Date newDate = newList.get(newPosition).getDate();
                    Date oldDate = mQuizList.get(oldPosition).getDate();
                    String newName = newList.get(newPosition).getName();
                    String oldName = mQuizList.get(oldPosition).getName();
                    return newDate.equals(oldDate) && newName.equals(oldName);
                }
            });
            mQuizList = newList;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public List<QuizEntry> getQuizList() {
        return mQuizList;
    }

    public void removeQuiz(int position) {
        mQuizList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreQuiz(QuizEntry quizEntry, int position) {
        mQuizList.add(position, quizEntry);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return mQuizList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameTv;
        TextView mDateTv;
        TextView mQuestionCountTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mNameTv = itemView.findViewById(R.id.name_tv);
            mDateTv = itemView.findViewById(R.id.date_tv);
            mQuestionCountTv = itemView.findViewById(R.id.question_count_tv);
        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            mListener.onQuizClick(mQuizList.get(getAdapterPosition()).getId());
        }
    }
}
