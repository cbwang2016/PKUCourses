# PKUCourses

## 点进去一个课程之后的代码结构：
* [ ] @HJX：点击一个item时，根据网页类型（文件列表类型 or Announcements类型 or MyGrades类型 or 咱们还没做的功能类型）新建合适的fragment，然后新建intent并将fragment传递到HJX做的CourseActionsActivity打开。有可能还要传递课程名称、课程course_id、页面标题之类的。
## Features and Roadmap
* [x] 课程列表 CourseListFragment
  * [x] 课程可置顶
    * [ ] 第一次进入应用提示可置顶
  * [x] 点进去之后显示啥 -- HJX
    * [ ] 获取课程的actions列表，部署了这个功能的就用合适的fragment，没部署的就通过SJY将要做的webview fragment查看。 -- HJX
    * [ ] 新增内容 -- HJX
    * [ ] 公告/通知 -- SJY
    * [ ] 信息/内容 -- ZRB
      * [ ] PDF/zip/MP3等文件的应用外打开-- WCB
      * [ ] 遇到文件夹可以点进去 -- WCB
      * [ ] 下载进度管理-- WCB
    * [ ] 我的成绩 -- SJY
    * [ ] 其他（咱们还没做的功能通过webview网页查看。注意要把session_id传递到Webview里） -- SJY
* [x] 公告 AnnouncementListFragment -- SJY
  * [ ] 未读内容标记
  * [ ] 未读内容提醒
* [ ] 通知板 NotificationsFragment -- HJX
  * [ ] 新增内容
  * [ ] 未读内容标记
  * [ ] 未读内容提醒
* [ ] 我的成绩 MyGradeFragment -- SJY
  * [ ] 课程列表
* [ ] 已下载文件的管理 DownloadedFilesListFragment -- WCB
  * [ ] 删除操作
* [x] 设置 SettingsActivity -- WCB
  * [ ] 自动更新
  * [x] 启动页面位置
  * [ ] 意见反馈 -- ZRB
  * [ ] About --ZRB
  * [ ] Github Page

