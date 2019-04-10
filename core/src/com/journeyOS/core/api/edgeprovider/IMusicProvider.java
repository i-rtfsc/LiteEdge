/*
 * Copyright (c) 2019 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.journeyOS.core.api.edgeprovider;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.database.music.Music;

public interface IMusicProvider extends ICoreApi {

    /**
     * 获取音乐播放器的信息
     *
     * @param packageName 音乐播放器的包名
     * @return 音乐播放器的信息
     */
    Music getConfig(String packageName);

    /**
     * 更新音乐播放器的信息
     *
     * @param music 音乐播放器的信息
     */
    void insertOrUpdateMusic(Music music);

    /**
     * 删除音乐播放器的信息
     *
     * @param music 音乐播放器的信息
     */
    void deleteMusic(Music music);

    /**
     * 删除全部悬浮球的配置
     */
    void deleteAll();
}
