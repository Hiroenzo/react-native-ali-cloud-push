require "json"
version = JSON.parse(File.read("package.json"))["version"]

Pod::Spec.new do |spec|

  spec.name         = "RCTAliCloudPush"
  spec.version      = version
  spec.summary      = "A short description of RCTAliCloudPush."
  spec.homepage     = "https://github.com/Hiroenzo/react-native-ali-cloud-push"
  spec.license      = "MIT"
  spec.author             = { "Hiroenzo" => "kyriechung@outlook.com" }
  spec.ios.deployment_target = "11.0"
  spec.tvos.deployment_target = "11.0"
  spec.source         = { :git => 'https://github.com/Hiroenzo/react-native-ali-cloud-push.git', :tag => "v#{spec.version}"}
  spec.source_files  =  "ios/**/*.{h,m}"
  spec.vendored_frameworks = "ios/libs/AlicloudUtils.framework","ios/libs/CloudPushSDK.framework","ios/libs/UTDID.framework","ios/libs/UTMini.framework","ios/libs/AlicloudSender.framework","ios/libs/EMASRest.framework"
  spec.libraries = "z", "resolv", "sqlite3"

  spec.requires_arc = true

  spec.dependency "React"
end
