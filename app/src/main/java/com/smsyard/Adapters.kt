package com.smsyard
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*

class HeaderAdapter: ListAdapter<String, HeaderAdapter.Holder>(DIFF){
    companion object{
        val DIFF=object: DiffUtil.ItemCallback<String>(){
            override fun areItemsTheSame(o:String,n:String)=o==n
            override fun areContentsTheSame(o:String,n:String)=o==n
        }
    }
    inner class Holder(v:View):RecyclerView.ViewHolder(v){
        val tv:TextView=v.findViewById(R.id.textHeader)
        fun bind(s:String){tv.text=s}
    }
    override fun onCreateViewHolder(p:ViewGroup,t:Int)=Holder(
        LayoutInflater.from(p.context).inflate(R.layout.item_header,p,false))
    override fun onBindViewHolder(h:Holder,pos:Int)=h.bind(getItem(pos))
}

class MessageAdapter(private val mark:(Message)->Unit):
    ListAdapter<Message,MessageAdapter.Holder>(DIFF){
    companion object{
        val DIFF=object: DiffUtil.ItemCallback<Message>(){
            override fun areItemsTheSame(o:Message,n:Message)=o.id==n.id
            override fun areContentsTheSame(o:Message,n:Message)=o==n
        }
    }
    inner class Holder(v:View):RecyclerView.ViewHolder(v){
        val body:TextView=v.findViewById(R.id.textBody)
        val btn:ImageButton=v.findViewById(R.id.buttonMark)
        fun bind(m:Message){
            body.text=m.body
            if(m.paid){
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.green_paid))
                btn.setImageResource(android.R.drawable.checkbox_on_background)
            }else{
                itemView.setBackgroundColor(0)
                btn.setImageResource(android.R.drawable.checkbox_off_background)
                btn.setOnClickListener{mark(m)}
            }
        }
    }
    override fun onCreateViewHolder(p:ViewGroup,t:Int)=Holder(
        LayoutInflater.from(p.context).inflate(R.layout.item_message,p,false))
    override fun onBindViewHolder(h:Holder,pos:Int)=h.bind(getItem(pos))
}
