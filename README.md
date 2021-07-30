#### 清除脏代码的AndroidStudio插件
```
项目历史原因导致很多脏代码的存在，实际上并未使用的类会被打包进APK，无形中增加了APK的体积。
使用该插件可以清除掉未被使用到的代码。
```
#### 一. 功能介绍
##### 支持功能
* 在AndroidStudio中安装使用该插件，一键清除脏代码；
* 支持清除Java代码；
* 支持清除kotlin代码；
* 自定义View仅在xml中使用时也会被过滤掉不被删除；
* 某一个未被使用的类中引入大量未在其他任何地方使用的类，会先删除该类，再删除引入的其他类；
* 清除完成之后会在根目录生成文件Temp_CleanDirtyCode/del.txt用于查看被删除的类。
##### 待优化功能
* 对于类名特别简单的，如A.java或B.kt这种，无法清除；
* 两个都未被使用的类循环调用时无法清除【✨✨✨已有思路，针对单个类被使用过的类记录，然后再对其逐个判断，形成环路的则删除整条链路中的所有类。有空再完善✨✨✨】；
* 对于在Manifest中注册的Activity、BroadcastReceiver、Service、ContentProvider等，无论实际使用与否，都保留未做删除；
* 考虑使用反射时包名等的拼接，所以保险起见，在类中仅用SimpleName做了判断，可能会有误判保留；
* 不支持引入库的扫描。
#### 二. 实现原理
```
1. 忽略根目录下的gradle、.gradle、libs、.idea、androidTest四个文件夹中的内容；

2. 扫描Java或kotlin文件，得到Temp_CleanDirtyCode/codePath.txt；

3. 扫描AndroidManifest文件，得到Temp_CleanDirtyCode/manifestPath.txt；

4. 扫描xml文件，得到Temp_CleanDirtyCode/layoutPath.txt；

5. 读取Temp_CleanDirtyCode/codePath.txt里面的每一行内容，分别在codePath.txt（除当前行）、manifestPath.txt、layoutPath.txt中判断是否又被使用；

6. 循环执行5，防止某一个未被使用的类里面引用了很多在其他任何地方都未被使用的类；

7. 删除完成后仅保留Temp_CleanDirtyCode/del.txt。
```
#### 二. 具体使用
##### 直接使用jar
```
1. 下载 [CleanDirtyCode.jar](https://github.com/xjz-111/CleanDirtyCode/blob/master/CleanDirtyCode.jar)  ；

2. 将下载的jar已插件形式安装在AndroidStudio中；

3. 点击Refactor，第一个便为CleanDirtyCode，点击可开始清理。
```
![image](https://github.com/xjz-111/CleanDirtyCode/blob/master/img/use.png)

##### 自行导出jar
```
1. 可使用IntelliJ IDEA打开项目；

2. 点击Build -> Prepare Plugin Module 'CleanDirtyCode' For Deployment 生成jar，存于项目根目录下。
```
![image](https://github.com/xjz-111/CleanDirtyCode/blob/master/img/export.png)


