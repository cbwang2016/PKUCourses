package about_annexcall;

public class PDF {
}

/**
 * Android 系统天然不支持 PDF 文件的阅读，因此，Android 应用中要实现 PDF 阅读功能一般有以下方案：
 *
 * WebView 中调用 GoogleDocs
 * 调起第三方支持 PDF 阅读的应用
 * 集成第三方 PDF SDK，在 Native 页面中阅读
 * 集成第三方 JS PDF SDK，在 WebView 页面中阅读
 * 将 PDF 文件转换成 HTML 或者图片等格式文件
 * ---------------------
 * 作者：ace1985
 * 来源：CSDN
 * 原文：https://blog.csdn.net/asce1885/article/details/52878066
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */

//--------------------------------------------------------------------------------------------------

//WebView 中调用 GoogleDocs
//这是最简单的一种方式，利用 GoogleDocs 提供的能力，通过 Android 的 WebView 即可实现打开在线 PDF 文档，代码如下所示：
//
//public void setDocumentPath(final String path) {
//    WebView webView = (WebView) findViewById(R.id.webview);
//    webView.getSettings().setJavaScriptEnabled(true);
//    webView.getSettings().setPluginsEnabled(true);
//    webView.loadUrl("https://docs.google.com/viewer?url=http://www.asce1885.com/cms/wwwroot/ng/downLoad/011615200732.pdf");
//}
//这种方案存在的问题是国内通常情况下访问不了 Google 提供 的服务，因此这种方案可以不考虑。

//--------------------------------------------------------------------------------------------------

//调起第三方支持 PDF 阅读的应用
//可行方案中最简单的一种方式，缺点是会跳出自己的应用转到第三方应用中，而且需要具备以下两个条件：
//
//PDF 文件需要下载到本地，不支持在线阅读
//用户手机中安装了支持 PDF 阅读的应用
//实现这个方案的代码示例如下：
//
//public Intent getPdfFileIntent(File file) {
//    Intent intent = new Intent("android.intent.action.VIEW");
//    intent.addCategory("android.intent.category.DEFAULT");
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    Uri uri = Uri.fromFile(file);
//    intent.setDataAndType(uri, "application/pdf");
//    return Intent.createChooser(intent, "Open File");
//}

//--------------------------------------------------------------------------------------------------

//集成第三方 PDF SDK，在 Native 页面中阅读
//第三方提供了很多免费或者付费的 PDF SDK，但在功能和性能等指标可能存在较大的区别，付费 SDK 的各项指标肯定是最优的。集成 Native SDK 的优点是体验好，缺点是会显著的增加包大小。目前可用的 SDK 主要有：
//
//Foxit 福昕 SDK1：国内老牌的付费 PDF SDK，功能强大，如果 PDF 阅读功能在你的应用中比较常用但又不是核心功能，可以考虑接入它。
//PlugPDF2：国外的一款付费 PDF SDK，类似 Foxit SDK，具体区别可以下载 Demo 试用下。
//PDFium3：Google 和 Foxit 合作开源的 Foxit 的 PDF 源码，作为 Chrome 浏览器的 PDF 渲染引擎组件，当然这是 C/C++ 实现的。
//PdfiumAndroid4：mshockwave 基于 PDFium 基础上适配 Android 平台的函数库，barteksc 在这个基础上再做了一些修改。
//AndroidPdfViewer5：barteksc 基于 PdfiumAndroid 基础上实现的一个 PDF 阅读 Demo，支持常见的手势，缩放，双击等效果。
//Native 方式的 PDF SDK 当然还有很多，但都存在一个共同的缺点，前面也说过，就是会显著增加包大小，例如 AndroidPdfViewer 的引入，剔除不常用的处理器架构，只保留 armeabi-v7a 和 x86，还是会增加将近 10M 的大小。

//--------------------------------------------------------------------------------------------------

//集成第三方 JS PDF SDK，在 WebView 页面中阅读
//目前 Android 平台上可用的第三方 JS PDF SDK 只有 mozilla 开源的 PDF.js，有服务端和客户端集成两种方式可以实现在 WebView 中打开 PDF 文件。

//--------------------------------------------------------------------------------------------------

//服务端方式
//PDF.js6 提供了一套较完善的在 H5 页面中阅读 PDF 的方案，同时支持 Web 前端，Android 和 iOS WebView 加载。服务部署起来应该也比较简单，大致的方案如下：
//
//客户端获取到在线 PDF 的链接
//将该链接作为参数，通过 WebView 向服务端的 PDF 服务发起请求
//PDF 服务将该链接的 PDF 文件下载到服务端缓存目录，并调用 PDF.js 提供的能力将 PDF 渲染出来。
//更具体的方案需要找服务端同学讨论确定。官方提供的 Demo 如下，可以通过手机的浏览器访问看效果：http://mozilla.github.io/pdf.js/web/viewer.html，当然在国内访问会有点慢。

//--------------------------------------------------------------------------------------------------

//客户端方式
//PDF.js 也支持客户端集成方式，当然需要做的工作比服务端集成方式多，也会给客户端起码增加 1～2M 的体积。
// 客户端需要把官方提供的 pdf.js 和 pdf.worker.js 拷贝到工程的 assets 目录，
// 同时在客户端本地实现一个离线 H5 页面，该页面通过上述两个 js 文件实现 PDF 的阅读。
// H5 页面的交互和设计需要设计师给出来，同时可能需要前端同学实现。

//--------------------------------------------------------------------------------------------------

//将 PDF 文件转换成 HTML 或者图片等格式文件
//这个方案是一位同事给出来的，一种可行的方案是将 PDF 文件通过 pdf2htmlEX7 转换成 HTML 格式文件，这样就可以很方便的使用 WebView 进行加载。而且这种格式转换很完美，几乎和原来的 PDF 文件排版一致。这种方案当然也是通过服务端实现，在服务端将对应的 PDF 文件或者链接转换成 HTML 格式的链接，然后客户端 WebView 进行加载显示即可。

//--------------------------------------------------------------------------------------------------

//总结
//在上面给出的方案中，如果允许 PDF 阅读跳出我们自己的应用，那么 调起第三方支持 PDF 阅读的应用 这种方案是首选；如果需要自己实现 PDF 阅读功能，那么需要根据具体业务需求来选择，如果要求在线阅读简单的 PDF 文件，那么可选上述两种服务端实现方案，如果要阅读本地 PDF 文件，那么可优先选择 PDF.js 的客户端方式，毕竟增加的包大小在可接受的范围，当然如果你的应用的主要功能就是阅读功能，那么可能需要选择用 Native 方式进行 PDF 的阅读。

//--------------------------------------------------------------------------------------------------
//作者：ace1985
//来源：CSDN
//原文：https://blog.csdn.net/asce1885/article/details/52878066
//版权声明：本文为博主原创文章，转载请附上博文链接！