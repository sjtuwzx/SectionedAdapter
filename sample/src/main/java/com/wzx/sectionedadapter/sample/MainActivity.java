package com.wzx.sectionedadapter.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.wzx.sectionedadapter.SectionedListAdapter;
import com.wzx.sectionedadapter.extra.InsertItemAdapter;
import com.wzx.sectionedadapter.sample.adapter.GridInsertItemCreator;
import com.wzx.sectionedadapter.sample.adapter.ItemCollapseAdapter;
import com.wzx.sectionedadapter.sample.adapter.ItemsAdapter;
import com.wzx.sectionedadapter.sample.adapter.section.HeaderSectionCreator;
import com.wzx.sectionedadapter.sample.adapter.section.UnRecycleSectionHeaderCreator;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private SectionedListAdapter mSectionedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        buildAdapter();
        mListView.setAdapter(mSectionedListAdapter);
    }

    private void buildAdapter() {
        mSectionedListAdapter = new SectionedListAdapter();
        UnRecycleSectionHeaderCreator unRecycleSectionHeaderCreator = new UnRecycleSectionHeaderCreator(this);
        addSection(unRecycleSectionHeaderCreator, null);

        ItemsAdapter itemAdapter = new ItemsAdapter(this);
        for (int i = 0; i < 6; i++) {
            itemAdapter.add(String.format("item[%d]", i + 1));
        }
        InsertItemAdapter insertItemAdapter = new InsertItemAdapter(itemAdapter);
        insertItemAdapter.addInsertItem(3, new GridInsertItemCreator(this));
        insertItemAdapter.addInsertItem(6, new GridInsertItemCreator(this));
        //position > adapter.getCount(),不展示
        insertItemAdapter.addInsertItem(9, new GridInsertItemCreator(this));
        addSection(null, insertItemAdapter);

        for (int i = 0; i < 10; i++) {
            HeaderSectionCreator headerSectionCreator = new HeaderSectionCreator(this, i + 1);
            ItemsAdapter adapter = new ItemsAdapter(this);
            for (int j = 0; j <= i; j++) {
                adapter.add(String.format("item[%d]", j + 1));
            }
            ItemCollapseAdapter collapseAdapter = new ItemCollapseAdapter(this, adapter);
            collapseAdapter.setup(3, true);

            SectionedListAdapter.SectionInfo sectionInfo = new SectionedListAdapter.SectionInfo.Builder()
                    .setHeaderCreator(headerSectionCreator)
                    .setAdapter(collapseAdapter)
                    .pinnedHeader()
                    .setIsExpanded(i < 5)
                    .build();
            headerSectionCreator.setSectionInfo(sectionInfo);
            mSectionedListAdapter.addSection(sectionInfo);
        }
    }

    private void addSection(SectionedListAdapter.SectionInfo.HeaderCreator headerCreator, BaseAdapter adapter) {
        SectionedListAdapter.SectionInfo sectionInfo = new SectionedListAdapter.SectionInfo.Builder()
                .setHeaderCreator(headerCreator)
                .setAdapter(adapter)
                .build();
        mSectionedListAdapter.addSection(sectionInfo);
    }
}
