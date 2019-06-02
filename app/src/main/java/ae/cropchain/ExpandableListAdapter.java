package ae.cropchain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private int HeaderCount = 0;
    private int CurrentHeaderCount = 0;
    private Context _context;
    private List<Header> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<Header, List<Product>> _listDataChild;
    private int rateDigits = 2;

    public ExpandableListAdapter(Context context, List<Header> listDataHeader,
                                 HashMap<Header, List<Product>> listChildData, int rateDigits) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.HeaderCount = listDataHeader.size() ;
        this.rateDigits = rateDigits;
    }

    @Override
    public Product getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Product proj = (Product)getChild(groupPosition, childPosition);
        String childText = proj.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        TextView txtListChild= (TextView) convertView
                .findViewById(R.id.lblListItem);
        TextView txtRate = (TextView) convertView.findViewById(R.id.lblListItem2);
        txtRate.setText(String.format("%0"+Integer.toString(rateDigits)+"d",(proj.getRate())));

        TextView txtDt = (TextView) convertView.findViewById(R.id.lblListItem3);
        txtDt.setText("Last Update "+Common.getDateFormatted(proj.getRateUpdatedAt(),"yyyy-MM-dd HH:mm:ss","dd/MM/YYYY"));


        if(childText.length() > 30){
            childText = childText.substring(0,30)+" ...";
        }


        txtListChild.setText(childText);
        txtListChild.setTag(proj);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        List<Product> projList = (List)this._listDataChild.get(this._listDataHeader.get(groupPosition));
        int vSize = 0;
        if(projList != null)
             vSize = projList.size();

        return vSize;
    }

    @Override
    public Header getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        if(!isExpanded)
        if(CurrentHeaderCount < HeaderCount) {
            ExpandableListView eLV = (ExpandableListView) parent;
            eLV.expandGroup(groupPosition);
            CurrentHeaderCount++;
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setNewItems(List<Header> listDataHeader, HashMap<Header, List<Product>> listChildData) {
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        CurrentHeaderCount = HeaderCount-1;
        notifyDataSetChanged();
    }



}