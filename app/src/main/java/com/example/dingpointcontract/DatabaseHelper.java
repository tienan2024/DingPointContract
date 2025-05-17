package com.example.dingpointcontract;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DingPointContract.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    public static final String TABLE_CONTRACT_TEMPLATES = "contract_templates";
    public static final String TABLE_CREATED_CONTRACTS = "created_contracts";
    public static final String TABLE_LEGAL_ARTICLES = "legal_articles";
    public static final String TABLE_SEARCH_INDEX = "search_index";

    // 通用列名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // 合同模板表列名
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_CATEGORY = "category";

    // 已创建合同表列名
    public static final String COLUMN_TEMPLATE_ID = "template_id";

    // 法条表列名
    public static final String COLUMN_ARTICLE_NUMBER = "article_number";

    // 搜索索引表列名
    public static final String COLUMN_TYPE = "type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建合同模板表
        String createContractTemplatesTable = "CREATE TABLE " + TABLE_CONTRACT_TEMPLATES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILE_NAME + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        // 创建已创建合同表
        String createCreatedContractsTable = "CREATE TABLE " + TABLE_CREATED_CONTRACTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILE_NAME + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_TEMPLATE_ID + " INTEGER, " +
                COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + COLUMN_TEMPLATE_ID + ") REFERENCES " + TABLE_CONTRACT_TEMPLATES + "(" + COLUMN_ID + "))";

        // 创建法条表
        String createLegalArticlesTable = "CREATE TABLE " + TABLE_LEGAL_ARTICLES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ARTICLE_NUMBER + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        // 创建搜索索引表（使用FTS4替代FTS5，兼容性更好）
        String createSearchIndexTable = "CREATE VIRTUAL TABLE " + TABLE_SEARCH_INDEX + " USING fts4(" +
                COLUMN_CONTENT + ", " +
                COLUMN_TITLE + ", " +
                COLUMN_FILE_NAME + ", " +
                COLUMN_ARTICLE_NUMBER + ", " +
                COLUMN_TYPE + ")";

        // 创建索引
        String createTemplatesFileNameIndex = "CREATE INDEX idx_templates_filename ON " +
                TABLE_CONTRACT_TEMPLATES + "(" + COLUMN_FILE_NAME + ")";
        String createContractsFileNameIndex = "CREATE INDEX idx_contracts_filename ON " +
                TABLE_CREATED_CONTRACTS + "(" + COLUMN_FILE_NAME + ")";
        String createArticlesNumberIndex = "CREATE INDEX idx_articles_number ON " +
                TABLE_LEGAL_ARTICLES + "(" + COLUMN_ARTICLE_NUMBER + ")";

        // 执行创建表语句
        db.execSQL(createContractTemplatesTable);
        db.execSQL(createCreatedContractsTable);
        db.execSQL(createLegalArticlesTable);
        db.execSQL(createSearchIndexTable);
        db.execSQL(createTemplatesFileNameIndex);
        db.execSQL(createContractsFileNameIndex);
        db.execSQL(createArticlesNumberIndex);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级逻辑
        if (oldVersion < 2) {
            // 未来版本升级时添加的代码
        }
    }

    // 添加合同模板
    public long addContractTemplate(String fileName, String title, String content, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_CATEGORY, category);

        long id = db.insert(TABLE_CONTRACT_TEMPLATES, null, values);

        // 同时更新搜索索引
        updateSearchIndex(id, title, content, fileName, null, "template");

        return id;
    }

    // 添加已创建合同
    public long addCreatedContract(String fileName, String title, String content, long templateId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TEMPLATE_ID, templateId);

        long id = db.insert(TABLE_CREATED_CONTRACTS, null, values);

        // 同时更新搜索索引
        updateSearchIndex(id, title, content, fileName, null, "contract");

        return id;
    }

    // 添加法条
    public long addLegalArticle(String articleNumber, String title, String content, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTICLE_NUMBER, articleNumber);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_CATEGORY, category);

        long id = db.insert(TABLE_LEGAL_ARTICLES, null, values);

        // 同时更新搜索索引
        updateSearchIndex(id, title, content, null, articleNumber, "article");

        return id;
    }

    // 更新搜索索引
    private void updateSearchIndex(long id, String title, String content, String fileName,
                                   String articleNumber, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_ARTICLE_NUMBER, articleNumber);
        values.put(COLUMN_TYPE, type);

        db.insert(TABLE_SEARCH_INDEX, null, values);
    }

    // 搜索功能
    public List<SearchResult> search(String query) {
        List<SearchResult> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results; // 返回空列表，避免空查询
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();

            // 尝试先用FTS4的MATCH语法
            try {
                String matchQuery = query + "*";
                String searchQuery = "SELECT * FROM " + TABLE_SEARCH_INDEX +
                        " WHERE " + TABLE_SEARCH_INDEX + " MATCH ?";

                cursor = db.rawQuery(searchQuery, new String[]{matchQuery});

                // 如果没有结果，回退到LIKE查询
                if (cursor.getCount() == 0) {
                    cursor.close();
                    cursor = null;
                    throw new Exception("无MATCH结果，回退到LIKE查询");
                }
            } catch (Exception e) {
                // 回退到简单LIKE查询，确保能工作
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }

                String likePattern = "%" + query + "%";
                String searchQuery = "SELECT * FROM " + TABLE_SEARCH_INDEX +
                        " WHERE " + COLUMN_TITLE + " LIKE ? OR " +
                        COLUMN_CONTENT + " LIKE ? OR " +
                        COLUMN_FILE_NAME + " LIKE ? OR " +
                        COLUMN_ARTICLE_NUMBER + " LIKE ?";

                cursor = db.rawQuery(searchQuery, new String[]{likePattern, likePattern, likePattern, likePattern});
            }

            // 处理搜索结果
            if (cursor != null) {
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
                int fileNameIndex = cursor.getColumnIndex(COLUMN_FILE_NAME);
                int articleNumberIndex = cursor.getColumnIndex(COLUMN_ARTICLE_NUMBER);
                int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);

                if (cursor.moveToFirst()) {
                    do {
                        SearchResult result = new SearchResult();

                        // 安全地获取列值，避免-1索引错误
                        if (titleIndex != -1) {
                            result.setTitle(cursor.getString(titleIndex));
                        }

                        if (contentIndex != -1) {
                            result.setContent(cursor.getString(contentIndex));
                        }

                        if (fileNameIndex != -1) {
                            result.setFileName(cursor.getString(fileNameIndex));
                        }

                        if (articleNumberIndex != -1) {
                            result.setArticleNumber(cursor.getString(articleNumberIndex));
                        }

                        if (typeIndex != -1) {
                            result.setType(cursor.getString(typeIndex));
                        }

                        results.add(result);
                    } while (cursor.moveToNext());
                }
            }

            Log.d("DatabaseHelper", "搜索完成，找到 " + results.size() + " 个结果");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "搜索时出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return results;
    }

    // 搜索结果类
    public static class SearchResult {
        private String title;
        private String content;
        private String fileName;
        private String articleNumber;
        private String type;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getArticleNumber() { return articleNumber; }
        public void setArticleNumber(String articleNumber) { this.articleNumber = articleNumber; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // 添加这个方法来修复旧版数据库问题
    public void fixDatabaseIfNeeded() {
        SQLiteDatabase db = null;
        boolean needsFix = false;

        try {
            db = getWritableDatabase();

            // 尝试查询搜索索引表
            db.rawQuery("SELECT * FROM " + TABLE_SEARCH_INDEX + " LIMIT 1", null);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "检测到数据库问题，需要修复: " + e.getMessage());
            needsFix = true;
        }

        if (needsFix) {
            try {
                Log.d("DatabaseHelper", "开始修复数据库...");

                // 删除旧搜索索引表
                try {
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_INDEX);
                } catch (Exception e) {
                    Log.e("DatabaseHelper", "删除旧索引表失败，继续: " + e.getMessage());
                }

                // 重新创建搜索索引表（使用FTS4）
                String createSearchIndexTable = "CREATE VIRTUAL TABLE " + TABLE_SEARCH_INDEX + " USING fts4(" +
                        COLUMN_CONTENT + ", " +
                        COLUMN_TITLE + ", " +
                        COLUMN_FILE_NAME + ", " +
                        COLUMN_ARTICLE_NUMBER + ", " +
                        COLUMN_TYPE + ")";

                db.execSQL(createSearchIndexTable);

                // 重新填充搜索索引表
                repopulateSearchIndex(db);

                Log.d("DatabaseHelper", "数据库修复完成");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "修复数据库失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 重新填充搜索索引
    private void repopulateSearchIndex(SQLiteDatabase db) {
        try {
            // 清空搜索索引表
            db.execSQL("DELETE FROM " + TABLE_SEARCH_INDEX);

            // 重新索引合同模板
            Cursor templateCursor = db.query(
                    TABLE_CONTRACT_TEMPLATES,
                    new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_FILE_NAME, COLUMN_CATEGORY},
                    null, null, null, null, null);

            if (templateCursor != null) {
                if (templateCursor.moveToFirst()) {
                    do {
                        int idIndex = templateCursor.getColumnIndex(COLUMN_ID);
                        int titleIndex = templateCursor.getColumnIndex(COLUMN_TITLE);
                        int contentIndex = templateCursor.getColumnIndex(COLUMN_CONTENT);
                        int fileNameIndex = templateCursor.getColumnIndex(COLUMN_FILE_NAME);

                        if (idIndex != -1 && titleIndex != -1 && contentIndex != -1 && fileNameIndex != -1) {
                            long id = templateCursor.getLong(idIndex);
                            String title = templateCursor.getString(titleIndex);
                            String content = templateCursor.getString(contentIndex);
                            String fileName = templateCursor.getString(fileNameIndex);

                            // 更新搜索索引
                            updateSearchIndex(id, title, content, fileName, null, "template");
                        }
                    } while (templateCursor.moveToNext());
                }
                templateCursor.close();
            }

            // 重新索引已创建合同
            Cursor contractCursor = db.query(
                    TABLE_CREATED_CONTRACTS,
                    new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_FILE_NAME},
                    null, null, null, null, null);

            if (contractCursor != null) {
                if (contractCursor.moveToFirst()) {
                    do {
                        int idIndex = contractCursor.getColumnIndex(COLUMN_ID);
                        int titleIndex = contractCursor.getColumnIndex(COLUMN_TITLE);
                        int contentIndex = contractCursor.getColumnIndex(COLUMN_CONTENT);
                        int fileNameIndex = contractCursor.getColumnIndex(COLUMN_FILE_NAME);

                        if (idIndex != -1 && titleIndex != -1 && contentIndex != -1 && fileNameIndex != -1) {
                            long id = contractCursor.getLong(idIndex);
                            String title = contractCursor.getString(titleIndex);
                            String content = contractCursor.getString(contentIndex);
                            String fileName = contractCursor.getString(fileNameIndex);

                            // 更新搜索索引
                            updateSearchIndex(id, title, content, fileName, null, "contract");
                        }
                    } while (contractCursor.moveToNext());
                }
                contractCursor.close();
            }

            // 重新索引法条
            Cursor articleCursor = db.query(
                    TABLE_LEGAL_ARTICLES,
                    new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_ARTICLE_NUMBER},
                    null, null, null, null, null);

            if (articleCursor != null) {
                if (articleCursor.moveToFirst()) {
                    do {
                        int idIndex = articleCursor.getColumnIndex(COLUMN_ID);
                        int titleIndex = articleCursor.getColumnIndex(COLUMN_TITLE);
                        int contentIndex = articleCursor.getColumnIndex(COLUMN_CONTENT);
                        int articleNumberIndex = articleCursor.getColumnIndex(COLUMN_ARTICLE_NUMBER);

                        if (idIndex != -1 && titleIndex != -1 && contentIndex != -1 && articleNumberIndex != -1) {
                            long id = articleCursor.getLong(idIndex);
                            String title = articleCursor.getString(titleIndex);
                            String content = articleCursor.getString(contentIndex);
                            String articleNumber = articleCursor.getString(articleNumberIndex);

                            // 更新搜索索引
                            updateSearchIndex(id, title, content, null, articleNumber, "article");
                        }
                    } while (articleCursor.moveToNext());
                }
                articleCursor.close();
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "重新填充搜索索引失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // 获取所有法条数据
    public List<LegalArticleData> getAllLegalArticles() {
        List<LegalArticleData> articles = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_LEGAL_ARTICLES, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int articleNumberIndex = cursor.getColumnIndex(COLUMN_ARTICLE_NUMBER);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);

                // 检查列索引是否有效
                if (articleNumberIndex == -1 || titleIndex == -1 ||
                        contentIndex == -1 || categoryIndex == -1) {
                    Log.e("DatabaseHelper", "法条表结构异常，列名索引无效");
                    return articles;
                }

                do {
                    String articleNumber = cursor.getString(articleNumberIndex);
                    String title = cursor.getString(titleIndex);
                    String content = cursor.getString(contentIndex);
                    String type = cursor.getString(categoryIndex);

                    articles.add(new LegalArticleData(articleNumber, title, content, type));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "获取法条数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return articles;
    }

    // 法条数据类
    public static class LegalArticleData {
        private String articleNumber;
        private String title;
        private String content;
        private String type;

        public LegalArticleData(String articleNumber, String title, String content, String type) {
            this.articleNumber = articleNumber;
            this.title = title;
            this.content = content;
            this.type = type;
        }

        public String getArticleNumber() {
            return articleNumber;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }


    /**
     * 根据法条号获取法条数据
     */
    public LegalArticleData getLegalArticleByNumber(String articleNumber) {
        if (articleNumber == null || articleNumber.isEmpty()) {
            return null;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        LegalArticleData article = null;

        try {
            db = this.getReadableDatabase();

            // 根据法条号查询
            cursor = db.query(
                    TABLE_LEGAL_ARTICLES,
                    null,
                    COLUMN_ARTICLE_NUMBER + " = ?",
                    new String[]{articleNumber},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);

                // 检查列索引是否有效
                if (titleIndex != -1 && contentIndex != -1 && categoryIndex != -1) {
                    String title = cursor.getString(titleIndex);
                    String content = cursor.getString(contentIndex);
                    String type = cursor.getString(categoryIndex);

                    article = new LegalArticleData(articleNumber, title, content, type);
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "获取法条数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return article;
    }
}
