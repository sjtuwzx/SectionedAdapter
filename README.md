# SectionedAdapter

多类型item的列表构建方案。可用于将各个type的item拆分成一个独立的单一职能的类，实现ListView的模块化

### 设计思路
将列表抽象成多个section，每个section由一个header和一个子adapter组成。在SectionedListAdapter中正确实现各个方法，使从ListView视角看到的是一个普通的多类型adapter。

优点：

* 自动控制“模块”的显隐
* 正确分发convertView，完美支持列表item的复用
* 可在页面初始化时通过近似配置化代码构建列表的全景图，然后通过数据控制列表的展现，构建以数据为中心的UI框架

### 适用场景

* 按一定顺序排列的多模组分段列表
* 二级列表，支持子列表的展开收起
* 在一个列表的任意指定位置插入其他类型的item
* 列表超过指定长度需要展开收起

### 使用方式

可参考sample中的MainActivity
```java
private void buildAdapter() {
    mSectionedListAdapter = new SectionedListAdapter();
    //添加一个不会被ListView回收复用的header
    UnRecycleSectionHeaderCreator unRecycleSectionHeaderCreator = new UnRecycleSectionHeaderCreator(this);
    addSection(unRecycleSectionHeaderCreator, null);

    ItemsAdapter itemAdapter = new ItemsAdapter(this);
    for (int i = 0; i < 6; i++) {
        itemAdapter.add(String.format("item[%d]", i + 1));
    }
    //在itemAdapter的指定位置插入多个其他类型的item
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
        //adapter.getCount()大于3时加入展开收起功能
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
``` 

通过上述代码构建了一个多类型列表，效果图如下

<img src="https://github.com/sjtuwzx/SectionedAdapter/blob/master/sample.png" width="320" />

## 参考
<https://github.com/JimiSmith/PinnedHeaderListView>
 