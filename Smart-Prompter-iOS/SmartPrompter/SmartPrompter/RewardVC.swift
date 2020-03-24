//
//  RewardVC.swift
//  SmartPrompter
//
//  Created by Likhon Gomes on 2/15/20.
//  Copyright © 2020 Likhon Gomes. All rights reserved.
//

import UIKit

class RewardVC: UIViewController {

    let circleHolder = UIView()
    let percentageLabel = UILabel()
    let button = UIButton()
    let textView = UITextView()
    var fraction = Double()
    let imageView = UIImageView()
    let imageArray = [#imageLiteral(resourceName: "meme2"),#imageLiteral(resourceName: "meme4"),#imageLiteral(resourceName: "meme6"),#imageLiteral(resourceName: "meme3"),#imageLiteral(resourceName: "meme5"),#imageLiteral(resourceName: "meme1")]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .white
        fraction = completedTask/totalTask
        
        textViewSetup()
        buttonSetup()
        if fraction == 1 {
            imageViewSetup()
            imageView.image = imageArray[Int.random(in: 0..<5)]
            textView.text = "All tasks completed! Well done!"
        } else {
            progressCircleSetup()
            textView.text = "You have completed another task! Well done!"
        }
        
        
        
        
        
        
    }

    
}

extension RewardVC {
    func imageViewSetup(){
        view.addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        imageView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        imageView.topAnchor.constraint(equalTo: textView.bottomAnchor, constant: 20).isActive = true
        imageView.bottomAnchor.constraint(equalTo: button.topAnchor, constant: -20).isActive = true
        imageView.contentMode = .scaleAspectFit
        imageView.image = #imageLiteral(resourceName: "meme2")
    }

    func progressCircleSetup(){
        let circlePath2 = UIBezierPath(arcCenter: view.center, radius: CGFloat(100), startAngle: CGFloat(Double.pi * 1.5), endAngle: CGFloat((Double.pi*1.5 + Double.pi * 2)), clockwise: true)
        

        let shapeLayer2 = CAShapeLayer()
        shapeLayer2.path = circlePath2.cgPath

        //change the fill color
        shapeLayer2.fillColor = UIColor.clear.cgColor
        //you can change the stroke color
        shapeLayer2.strokeColor = #colorLiteral(red: 0.8039215803, green: 0.8039215803, blue: 0.8039215803, alpha: 1)
        //you can change the line width
        shapeLayer2.lineWidth = 10.0

        view.layer.addSublayer(shapeLayer2)
        
        let circlePath = UIBezierPath(arcCenter: view.center, radius: CGFloat(100), startAngle: CGFloat(Double.pi * 1.5), endAngle: CGFloat((Double.pi*1.5 + Double.pi * 2*fraction)), clockwise: true)
        

        let shapeLayer = CAShapeLayer()
        shapeLayer.path = circlePath.cgPath

        //change the fill color
        shapeLayer.fillColor = UIColor.clear.cgColor
        //you can change the stroke color
        shapeLayer.strokeColor = UIColor.purple.cgColor
        //you can change the line width
        shapeLayer.lineWidth = 10.0

        view.layer.addSublayer(shapeLayer)
        
        view.addSubview(percentageLabel)
        percentageLabel.translatesAutoresizingMaskIntoConstraints = false
        percentageLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        percentageLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
        percentageLabel.text = "\(Int(fraction*100))%"
        percentageLabel.textColor = .purple
        percentageLabel.font = UIFont.boldSystemFont(ofSize: 50)
    }
    
    func buttonSetup(){
        view.addSubview(button)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        button.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        button.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        button.heightAnchor.constraint(equalToConstant: 60).isActive = true
        button.backgroundColor = .red
        button.setTitle("Return to Home", for: .normal)
        button.addTarget(self, action: #selector(buttonClicked), for: .touchUpInside)
    }
    
    @objc func buttonClicked(){
        dismiss(animated: true, completion: nil)
    }
    
    func textViewSetup(){
        view.addSubview(textView)
        textView.translatesAutoresizingMaskIntoConstraints = false
        textView.topAnchor.constraint(equalTo: view.topAnchor, constant: 40).isActive = true
        textView.leadingAnchor.constraint(lessThanOrEqualTo: view.leadingAnchor,constant: 20).isActive = true
        textView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20).isActive = true
        
        textView.font = UIFont.boldSystemFont(ofSize: 30)
        textView.backgroundColor = .clear
        textView.textAlignment = .center
        textView.heightAnchor.constraint(equalToConstant: 150).isActive = true
    }
}

