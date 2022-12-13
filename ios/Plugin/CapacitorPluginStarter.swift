import Foundation

@objc public class CapacitorPluginStarter: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
