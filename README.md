## DownloadManager 文件下载器
多线程下载，支持断点续传，分为Android和Java两个版本。

![效果图](screenshots/show.png)

使用案例:

```
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;

    private String tag1 = "";
    private String tag2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start1).setOnClickListener(this);
        findViewById(R.id.btn_pause1).setOnClickListener(this);
        findViewById(R.id.btn_cancel1).setOnClickListener(this);

        findViewById(R.id.btn_start2).setOnClickListener(this);
        findViewById(R.id.btn_pause2).setOnClickListener(this);
        findViewById(R.id.btn_cancel2).setOnClickListener(this);

        progressBar1 = (ProgressBar) findViewById(R.id.pb_download1);
        progressBar2 = (ProgressBar) findViewById(R.id.pb_download2);
    }

    @Override
    public void onClick(View view) {
        String name1 = "坂井泉水.mp4";
        String url1 = "http://220.194.199.176/4/k/l/p/d/klpdruzqjxgpkyoxeudmpjqvnwazxp/hc.yinyuetai.com/7348015EA9536F7A49FDD32FA0B025B2.mp4?sc=1e26e64ef11e8626&br=781&vid=3048701&aid=32393&area=ML&vst=0&ptp=mv&rd=yinyuetai.com";
        tag1 = String.valueOf(url1.hashCode());

        String name2 = "Burning-Maria Arredondo.mp4";
        String url2 = "http://220.194.199.185/1/e/e/u/i/eeuioqayptkihjaylfnmarwejpfwcx/hc.yinyuetai.com/2E67013A8E442003862822D8115227D6.flv?sc=053dee7e67c31614&br=777&vid=7621&aid=1802&area=US&vst=0&ptp=mv&rd=yinyuetai.com";
        tag2 = String.valueOf(url2.hashCode());

        switch (view.getId()) {
            case R.id.btn_start1:
                start(name1,url1,tag1);
                break;
            case R.id.btn_pause1:
                pause(tag1);
                break;
            case R.id.btn_cancel1:
                cancel(tag1);

            case R.id.btn_start2:
                start(name2,url2,tag2);
                break;
            case R.id.btn_pause2:
                pause(tag2);
                break;
            case R.id.btn_cancel2:
                cancel(tag2);
                break;
            default:
                break;

        }
    }

    private void start(String name,String url,String tag) {
        File folder = Environment.getExternalStorageDirectory();

        // 方式二(文本类型的内容获取)
        final RequestCall call = new GetBuilder()
                .name(name)
                .folder(folder)
                .uri(url)
                .tag(tag)
                .build();
        DownloadManager.getInstance(this).start(call, new FileCallBack() {
            @Override
            public void onStart(String tag) {
                L.d("=====> onStart " + tag);
                show("=====> onStart " + tag);
            }

            @Override
            public void onDownloadProgress(String tag,long finished, long totalLength, int percent) {
                L.d("=====> onDownloadProgress: " + percent);
                if (tag.equals(tag1)) {
                    progressBar1.setProgress(percent);
                } else if (tag.equals(tag2)) {
                    progressBar2.setProgress(percent);
                }
            }

            @Override
            public void onDownloadPaused() {
                L.d("=====> onDownloadPaused: " );
                show("=====> onDownloadPaused");
            }

            @Override
            public void onDownloadCanceled() {
                L.d("=====> onDownloadCanceled: " );
                show("=====> onDownloadCanceled");
            }

            @Override
            public void onDownloadFailed(DownloadException e) {
                L.d("=====> onDownloadFailed: " + e.getErrorMessage());
                show("=====> onDownloadFailed: " + e.getErrorMessage());
            }

            @Override
            public void onDownloadCompleted(File file) {
                L.d("=====> onDownloadCompleted: " + file.getAbsolutePath());
                show("=====> onDownloadCompleted " + file.getAbsolutePath());
            }
        });
    }

    private void pause(String tag) {
        DownloadManager.getInstance(this).pause(tag);
    }

    private void cancel(String tag) {
        DownloadManager.getInstance(this).cancel(tag);
    }

    private void show(String message) {
        Toast.makeText(MainActivity.this,message, Toast.LENGTH_SHORT).show();
    }
}
`
```

## 简单调用
如果只想获取下载进度和结果，还可以这样调用。


```
 new GetBuilder()
		.name("JOKER_山本彩.mp4")
		.folder(new File("F:/"))
		.uri(url)
		.tag(tag)
		.build()
		.execute(new FileCallBack() {
			@Override
			public void onStart(String tag) {
				// UI Thread
			}

			@Override
			public void onDownloadProgress(String tag, long finished, long totalLength, int percent) {
				// UI Thread
			}

			@Override
			public void onDownloadFailed(DownloadException e) {
				// UI Thread
			}

			@Override
			public void onDownloadCompleted(File file) {
				// UI Thread
			}
		});
```

## 取消一个下载任务
`DownloadManager.getInstance(this).cancel(tag);`

## 暂停一个下载任务
`DownloadManager.getInstance(this).pause(tag);`

## 取消所有下载任务
`DownloadManager.getInstance(this).pauseAll();`

## 暂停所有下载任务
`DownloadManager.getInstance(this).cancelAll();`

## 依赖
Android: 拷贝libs目录downloader-android-1.0.jar到项目引用即可。

Java: 拷贝libs目录downloader-java-1.0.jar到项目中，该jar需要额外引入sqlite-jdbc.jar，本项目使用的版本是sqlite-jdbc-3.8.11.1.jar。


