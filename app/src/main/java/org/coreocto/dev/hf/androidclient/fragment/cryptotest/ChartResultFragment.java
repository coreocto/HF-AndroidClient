package org.coreocto.dev.hf.androidclient.fragment.cryptotest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.benchmark.BenchmarkResult;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChartResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private BarChart mChart;
    private List<BenchmarkResult> resultList;

    public ChartResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartResultFragment newInstance(String param1, String param2) {
        ChartResultFragment fragment = new ChartResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart_result, container, false);

        mChart = (BarChart) view.findViewById(R.id.chart);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.setScaleEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = mChart.getAxisRight();
        yAxis.setEnabled(false);

        if (resultList != null) {
            List<IBarDataSet> sets = new ArrayList<IBarDataSet>();
            List<BarEntry> entries = new ArrayList<BarEntry>();
            final List<String> xLabel = new ArrayList<>();

            int j = 0;

            for (BenchmarkResult result : resultList) {
                entries.add(new BarEntry(j, (float) result.getTime()));
                int lastIdx = result.getType().lastIndexOf("/");
                xLabel.add(j, lastIdx == -1 ? result.getType() : result.getType().substring(0, lastIdx));
                j++;
            }

            BarDataSet ds = new BarDataSet(entries, "");
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);

            BarData d = new BarData(sets);
            mChart.setData(d);

            xAxis.setValueFormatter(new IndexAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value == (int) value) {
                        return xLabel.get((int) value);
                    } else {
                        return AppConstants.EMPTY_STRING;
                    }
                }
            });
        }

        mChart.invalidate();

        return view;
    }

    public void setBarChartData(List<BenchmarkResult> resultList) {
        this.resultList = resultList;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
